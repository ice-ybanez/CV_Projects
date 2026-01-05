package main

import (
	"bufio"
	"context"
	"fmt"
	"log"
	"os"
	"strings"
	"time"

	pb "spellingbee/proto"

	"google.golang.org/grpc"
)

// client communicates with the server

func main() {
	// Insecure connection for local development
	conn, err := grpc.Dial("localhost:50051", grpc.WithInsecure())
	if err != nil {
		log.Fatalf("dial: %v", err)
	}
	defer conn.Close()

	client := pb.NewSpellBeeClient(conn)

	// Ask the server to start a game with 5 seconds timeout
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	ng, err := client.NewGame(ctx, &pb.NewGameRequest{Player: "local"})
	if err != nil {
		log.Fatalf("NewGame: %v", err)
	}

	gameID := ng.GameId
	fmt.Println("Spelling Bee!")
	printPuzzle(ng.Puzzle)

	reader := bufio.NewReader(os.Stdin)
	for {
		fmt.Print("Enter word >>> ")
		line, _ := reader.ReadString('\n')
		w := strings.TrimSpace(line)
		if w == "" || w == "quit" {
			break
		}

		resp, err := client.SubmitWord(context.Background(), &pb.SubmitWordRequest{
			GameId: gameID,
			Word:   w,
		})

		if err != nil {
			fmt.Println("Error:", err)
			continue
		}
		fmt.Println(resp.Message)
		st, _ := client.GetStatus(context.Background(), &pb.GetStatusRequest{GameId: gameID})
		if st != nil {
			printPuzzle(st.Puzzle)
		}
	}
	fmt.Println("Goodbye! Thanks for playing Spelling Bee!")
}

// prints the puzzle to the console with format:
// D L [U] G E I T

func printPuzzle(p *pb.Puzzle) {
	l := p.Letters
	center := strings.ToUpper(p.Center)
	parts := []string{}
	for _, r := range l {
		if string(r) == center {
			parts = append(parts, fmt.Sprintf("[%c]", r))
		} else {
			parts = append(parts, fmt.Sprintf("%c", r))
		}
	}
	fmt.Println(strings.Join(parts, " "))
}
