package main

import (
	"encoding/json"
	"os"
	"sync"
)

// Stats holds tracked stats and saves to disk
type Stats struct {
	TotalGames    int `json:"total_games"`
	TotalWords    int `json:"total_words"`
	TotalPangrams int `json:"total_pangrams"`
	HighestScore  int `json:"highest_score"`
}

var (
	stats     Stats
	statsMu   sync.Mutex
	statsPath = "data/stats.json"
)

// LoadStats reads stats.json, else all zeros
func LoadStats() error {
	statsMu.Lock()
	defer statsMu.Unlock()

	f, err := os.Open(statsPath)
	if err != nil {
		if os.IsNotExist(err) {
			stats = Stats{} // all zeros
			return nil
		}
		return err
	}
	defer f.Close()

	dec := json.NewDecoder(f)
	return dec.Decode(&stats)
}

// saveStatsLocked writes the current stats to disk
// call only while holding statsMu
func saveStatsLocked() error {
	f, err := os.Create(statsPath)
	if err != nil {
		return err
	}
	defer f.Close()

	enc := json.NewEncoder(f)
	enc.SetIndent("", "  ")
	return enc.Encode(&stats)
}

// StatsNewGame increments TOTAL GAMES and SAVES
func StatsNewGame() error {
	statsMu.Lock()
	defer statsMu.Unlock()

	stats.TotalGames++
	return saveStatsLocked()
}

// StatsWordPlayed updates WORD COUNT, PANGRAM COUNT and HIGH SCORE
func StatsWordPlayed(finalScore int, wasPangram bool) error {
	statsMu.Lock()
	defer statsMu.Unlock()

	stats.TotalWords++
	if wasPangram {
		stats.TotalPangrams++
	}
	if finalScore > stats.HighestScore {
		stats.HighestScore = finalScore
	}
	return saveStatsLocked()
}
