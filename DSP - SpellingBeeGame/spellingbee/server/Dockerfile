# build and run - spellingbee gRPC server
FROM golang:1.23
WORKDIR /app

# copy go.mod and go.sum, download deps first
COPY go.mod go.sum ./
RUN go mod download

# copy src needed by server
COPY proto ./proto
COPY server ./server
COPY data ./data

RUN go build -o /app/spellbee-server ./server

# gRPC server listens on 50051
EXPOSE 50051

CMD ["/app/spellbee-server"]


