package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"strings"

	"github.com/streadway/amqp"
)

const rabbitMQURL = "amqp://admin:password@localhost:5672/"
const pangramExchange = "pangram_events"

// startPangramListener connects to RabbitMQ and listens for pangram broadcasts
// runs in a goroutine so the user can still type commands to the statsserver
func startPangramListener() {
	go func() {
		conn, err := amqp.Dial(rabbitMQURL)
		if err != nil {
			fmt.Println("RabbitMQ connect error:", err)
			return
		}
		defer conn.Close()

		ch, err := conn.Channel()
		if err != nil {
			fmt.Println("RabbitMQ channel error:", err)
			return
		}
		defer ch.Close()

		// fanout exchange for broadcasting pangram events
		err = ch.ExchangeDeclare(
			pangramExchange,
			"fanout",
			true,
			false,
			false,
			false,
			nil,
		)
		if err != nil {
			fmt.Println("RabbitMQ exchange declare error:", err)
			return
		}

		// each client gets its own auto-named, auto-deleted queue
		q, err := ch.QueueDeclare(
			"",    // name
			false, // durable
			true,  // auto-delete when last consumer disconnects
			true,  // exclusive
			false, // no-wait
			nil,
		)
		if err != nil {
			fmt.Println("RabbitMQ queue declare error:", err)
			return
		}

		// bind queue to the pangram exchange.
		err = ch.QueueBind(
			q.Name,
			"",
			pangramExchange,
			false,
			nil,
		)
		if err != nil {
			fmt.Println("RabbitMQ queue bind error:", err)
			return
		}

		msgs, err := ch.Consume(
			q.Name,
			"",    // consumer tag
			true,  // auto-ack
			true,  // exclusive
			false, // no-local
			false, // no-wait
			nil,
		)
		if err != nil {
			fmt.Println("RabbitMQ consume error:", err)
			return
		}

		fmt.Println("Listening for pangram events from RabbitMQ...")

		for m := range msgs {
			fmt.Printf("[PANGRAM EVENT] %s\n", string(m.Body))
		}
	}()
}

func main() {
	startPangramListener() // rabbitmq listener

	conn, err := net.Dial("tcp", "localhost:6000")
	if err != nil {
		fmt.Println("connect error:", err)
		return
	}
	defer conn.Close()

	fmt.Println("Connected to stats server.")
	fmt.Println("Commands: TOTAL_GAMES, TOTAL_WORDS, TOTAL_PANGRAMS, HIGHEST_SCORE, QUIT")

	stdin := bufio.NewReader(os.Stdin)
	srv := bufio.NewReader(conn)

	for {
		fmt.Print("> ")
		line, _ := stdin.ReadString('\n')
		cmd := strings.TrimSpace(line)
		if cmd == "" {
			continue
		}

		// send command to server
		fmt.Fprintf(conn, "%s\n", cmd)

		if strings.EqualFold(cmd, "QUIT") || strings.EqualFold(cmd, "EXIT") {
			break
		}

		// read one line back from server
		resp, err := srv.ReadString('\n')
		if err != nil {
			fmt.Println("Server closed connection")
			return
		}
		fmt.Print(resp)
	}
}
