# build and run - stats TCP server
FROM golang:1.23
WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

# copy statsserver and data only
COPY statsserver ./statsserver
COPY data ./data

RUN go build -o /app/stats-server ./statsserver

# statsserver listens on 6000
EXPOSE 6000

CMD ["/app/stats-server"]

