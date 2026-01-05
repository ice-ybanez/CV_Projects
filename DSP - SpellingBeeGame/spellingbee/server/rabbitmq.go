package main

import (
	"fmt"
	"log"
	"os"

	"github.com/streadway/amqp"
)

// rabbitMQURL is the connection string for RabbitMQ
var rabbitMQURL = getEnv("RABBITMQ_URL", "amqp://admin:password@localhost:5672/")

const pangramExchange = "pangram_events"

// when run locally, there's no RABBITMQ_URL env var
// when run in Docker, it's set by Docker Compose
func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

// publishPangramEvent sends a simple text message to the "pangram_events" fanout exchange
// connect, declare exchange, publish, close
// if RabbitMQ is not running, log the error and continue the game anyway
func publishPangramEvent(gameID string, word string, totalScore int32) {
	conn, err := amqp.Dial(rabbitMQURL)
	if err != nil {
		log.Println("RabbitMQ dial error:", err)
		return
	}
	defer conn.Close()

	ch, err := conn.Channel()
	if err != nil {
		log.Println("RabbitMQ channel error:", err)
		return
	}
	defer ch.Close()

	// declare a fanout exchange for broadcasts
	err = ch.ExchangeDeclare(
		pangramExchange, // name
		"fanout",        // type
		true,            // durable
		false,           // auto-deleted
		false,           // internal
		false,           // no-wait
		nil,             // args
	)
	if err != nil {
		log.Println("RabbitMQ exchange declare error:", err)
		return
	}

	body := fmt.Sprintf("Pangram found! Game=%s, Word=%s, TotalScore=%d", gameID, word, totalScore)

	err = ch.Publish(
		pangramExchange, // exchange
		"",              // routing key
		false,           // mandatory
		false,           // immediate
		amqp.Publishing{
			ContentType: "text/plain",
			Body:        []byte(body),
		},
	)
	if err != nil {
		log.Println("RabbitMQ publish error:", err)
		return
	}

	log.Println("Published pangram event to RabbitMQ:", body)
}
