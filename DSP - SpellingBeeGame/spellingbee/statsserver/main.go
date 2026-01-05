package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"log"
	"net"
	"os"
	"strings"
)

// Stats the same struct shape as server/stats.go so the JSON matches
type Stats struct {
	TotalGames    int `json:"total_games"`
	TotalWords    int `json:"total_words"`
	TotalPangrams int `json:"total_pangrams"`
	HighestScore  int `json:"highest_score"`
}

func loadStats() (Stats, error) {
	file, err := os.Open("data/stats.json")
	if err != nil {
		return Stats{}, err
	}
	defer file.Close()

	var s Stats
	if err := json.NewDecoder(file).Decode(&s); err != nil {
		return Stats{}, err
	}
	return s, nil
}

func handleConn(conn net.Conn) {
	defer conn.Close()

	r := bufio.NewReader(conn)
	for {
		line, err := r.ReadString('\n')
		if err != nil {
			return // client closed
		}
		cmd := strings.TrimSpace(strings.ToUpper(line))

		s, err := loadStats()
		if err != nil {
			fmt.Fprintf(conn, "ERROR reading stats: %v\n", err)
			continue
		}

		switch cmd {
		case "TOTAL_GAMES":
			fmt.Fprintf(conn, "Total games: %d\n", s.TotalGames)
		case "TOTAL_WORDS":
			fmt.Fprintf(conn, "Total words: %d\n", s.TotalWords)
		case "TOTAL_PANGRAMS":
			fmt.Fprintf(conn, "Total pangrams: %d\n", s.TotalPangrams)
		case "HIGHEST_SCORE":
			fmt.Fprintf(conn, "Highest score: %d\n", s.HighestScore)
		case "QUIT", "EXIT":
			fmt.Fprintln(conn, "Goodbye.")
			return
		default:
			fmt.Fprintln(conn, "Unknown command. Try: TOTAL_GAMES, TOTAL_WORDS, TOTAL_PANGRAMS, HIGHEST_SCORE, QUIT.")
		}
	}
}

func main() {
	ln, err := net.Listen("tcp", ":6000")
	if err != nil {
		log.Fatal("listen:", err)
	}
	log.Println("Stats server listening on :6000")

	for {
		conn, err := ln.Accept()
		if err != nil {
			log.Println("accept:", err)
			continue
		}
		// handle multiple clients concurrently with goroutines
		go handleConn(conn)
	}
}
