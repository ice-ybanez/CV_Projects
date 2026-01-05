package main

// Implementation of the Spelling Bee gRPC service
// Also holds the game state

import (
	"context"
	"errors"
	"fmt"
	"math/rand"
	"strings"
	"sync"
	"time"

	pb "spellingbee/proto"
)

// game-state kept on server

type gameState struct {
	PuzzleLetters string              // 7 letters in uppercase e.g., "DLUGEIT"
	Center        rune                // center letter e.g., "U"
	Total         int32               // score so far
	Found         map[string]struct{} //already found words
	mu            sync.Mutex          // protects Found and Total fields from concurrent access (goroutines)
}

// BeeService wires dependencies and implements the RPCs.

type BeeService struct {
	pb.UnimplementedSpellBeeServer

	// dependencies (in NewBeeService)
	cache *DictionaryCache // Singleton (dictionary + pangrams)
	rules RuleSet          // from Factory, RuleSet

	// in-memory games keyed by game_id
	games map[string]*gameState
	mu    sync.Mutex
}

// NewBeeService sets up the dependencies

func NewBeeService() (*BeeService, error) {
	cache, err := GetDictionaryCache() // Singleton loads JSON once and caches
	if err != nil {
		return nil, err
	}

	rules := NewRuleSetFactory().Standard() // returns a RuleSet

	return &BeeService{
		cache: cache,
		rules: rules,
		games: make(map[string]*gameState),
	}, nil
}

// NewGame creates a new game by selecting a pangram, builds a 7-letter puzzle and returns a new game_id

func (b *BeeService) NewGame(ctx context.Context, req *pb.NewGameRequest) (*pb.NewGameResponse, error) {
	pangrams := b.cache.Pangrams.All()

	if len(pangrams) == 0 {
		return nil, errors.New("no pangrams available")
	}

	rand.Seed(time.Now().UnixNano())

	p := pangrams[rand.Intn(len(pangrams))]

	// build letters from the pangram
	letSet := uniqueLetters(p)
	letters := make([]rune, 0, 7)
	for r := range letSet {
		letters = append(letters, r)
	}
	// shuffle the order of letters and pick the center
	rand.Shuffle(len(letters), func(i, j int) {
		letters[i], letters[j] = letters[j], letters[i]
	})

	center := letters[rand.Intn(7)]

	// build display letters in uppercase
	var sb strings.Builder

	for _, r := range letters {
		sb.WriteRune(toUpper(r))
	}

	gameID := newID()

	st := &gameState{
		PuzzleLetters: sb.String(),
		Center:        toUpper(center),
		Found:         make(map[string]struct{}),
	}

	b.mu.Lock()
	b.games[gameID] = st
	b.mu.Unlock()

	// update stats: one more game started
	_ = StatsNewGame()

	return &pb.NewGameResponse{
		GameId: gameID, // return the game_id in CamelCase to match proto
		Puzzle: &pb.Puzzle{
			Letters: st.PuzzleLetters,
			Center:  string(st.Center),
		},
		Total: 0,
	}, nil
}

// SubmitWord validates a word and scores a word submission through the RuleSet

func (b *BeeService) SubmitWord(ctx context.Context, req *pb.SubmitWordRequest) (*pb.SubmitWordResponse, error) {
	st := b.getGame(req.GameId)
	if st == nil {
		return nil, errors.New("unknown game_id")
	}

	word := strings.ToLower(strings.TrimSpace(req.Word))
	if word == "" {
		return &pb.SubmitWordResponse{Valid: false, Reason: "empty word", Message: "Empty word", Points: 0, Total: 0}, nil
	}

	st.mu.Lock()
	defer st.mu.Unlock()

	if _, exists := st.Found[word]; exists {
		return &pb.SubmitWordResponse{
			Valid:   false,
			Reason:  "already found",
			Message: "Already found. Current score: " + fmt.Sprint(st.Total),
			Points:  0,
			Total:   st.Total,
		}, nil
	}

	// move all rule checks to the Factory RuleSet

	v := b.rules.ValidateAndScore(word, st.PuzzleLetters, st.Center, b.cache.Dictionary)
	if !v.Valid {
		msg := v.Reason
		if v.MissingCenter {
			msg = fmt.Sprintf("Invalid word, missing centre letter %s.", string(st.Center))
		}
		return &pb.SubmitWordResponse{
			Valid:   false,
			Reason:  v.Reason,
			Message: msg,
			Points:  0,
			Total:   st.Total,
		}, nil
	}

	st.Total += int32(v.Points)
	st.Found[word] = struct{}{}

	_ = StatsWordPlayed(int(st.Total), v.Pangram) // update the global stats file

	// if the submitted word is a pangram, broadcast an event via RabbitMQ
	if v.Pangram {
		publishPangramEvent(req.GameId, word, st.Total)
	}

	line := ""
	if v.Pangram {
		line = fmt.Sprintf("Pangram! %d points. Current score: %d", v.Points, st.Total)
	} else {
		line = fmt.Sprintf("Valid word scoring %d points. Current score: %d", v.Points, st.Total)
	}

	return &pb.SubmitWordResponse{
		Valid:   true,
		Message: line,
		Points:  int32(v.Points),
		Total:   st.Total,
		Pangram: v.Pangram,
	}, nil
}

func (b *BeeService) GetStatus(ctx context.Context, req *pb.GetStatusRequest) (*pb.GetStatusResponse, error) {
	st := b.getGame(req.GameId)
	if st == nil {
		return nil, errors.New("unknown game_id")
	}
	st.mu.Lock()
	defer st.mu.Unlock()

	found := make([]string, 0, len(st.Found))
	for w := range st.Found {
		found = append(found, w)
	}
	return &pb.GetStatusResponse{
		Puzzle: &pb.Puzzle{Letters: st.PuzzleLetters, Center: string(st.Center)},
		Total:  st.Total,
		Found:  found,
	}, nil
}

// helper functions
func (b *BeeService) getGame(id string) *gameState {
	b.mu.Lock()
	defer b.mu.Unlock()
	return b.games[id]
}

func uniqueLetters(s string) map[rune]struct{} {
	m := make(map[rune]struct{}, 7)
	for _, r := range strings.ToLower(s) {
		if r >= 'a' && r <= 'z' {
			m[r] = struct{}{}
		}
	}
	return m
}

func toUpper(r rune) rune {
	if r >= 'a' && r <= 'z' {
		return r - 32
	}
	return r
}

func toLower(r rune) rune {
	if r >= 'A' && r <= 'Z' {
		return r + 32
	}
	return r
}

func newID() string {
	const alpha = "abcdefghijklmnopqrstuvwxyz0123456789"
	b := make([]byte, 12)
	for i := range b {
		b[i] = alpha[rand.Intn(len(alpha))]
	}
	return string(b)
}
