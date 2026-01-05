package main

import (
	"log"
	"net"

	pb "spellingbee/proto"

	"google.golang.org/grpc"
)

// Starts a gRPC server that serves the SpellBee service.

func main() {
	// Load stats from a file
	if err := LoadStats(); err != nil {
		log.Fatalf("load stats: %v", err)
	}

	// Listen on TCP port 50051 (localhost)
	lis, err := net.Listen("tcp", ":50051")
	if err != nil {
		log.Fatalf("listen: %v", err)
	}

	// Create a new gRPC server
	grpcServer := grpc.NewServer()

	// Create a new SpellBee service with dependencies
	svc, err := NewBeeService()
	if err != nil {
		log.Fatalf("init service: %v", err)
	}

	// Register the service with the gRPC server
	pb.RegisterSpellBeeServer(grpcServer, svc)

	log.Println("SpellBee server listening on :50051")
	if err := grpcServer.Serve(lis); err != nil {
		log.Fatalf("serve: %v", err)
	}
}
