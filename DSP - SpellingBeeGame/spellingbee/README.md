# Spelling Bee

**Student Name:** Ice Ybañez\
**Student Number:** R00176611

- **Assignment 1:** gRPC-based single-player Spelling Bee game.
- **Assignment 2:** extends Assignment 1 with persistent statistics, 
              a socket-based client-server stats app, 
              RabbitMQ pangram event queue and
              Docker.

---------

## Updates for Assignment 2
### 1. Persistent Statistics - stats file
**New files:** ``server/stats.go``, ``data/stats.json``

The gRPC server now keeps global statistics in ``data/stats.json``:

````
- total_games 
- total_words
- total_pangrams
- highest_score
````

Updates happen in:
- **NewGame**(``server/service.go``) calls **StatsNewGame**(``server/stats.go``) which increments ``total_games``.
- **SubmitWord**(``server/service.go``), after a valid word is submitted, **StatsWordPlayed**(``server/stats.go``) is called to update the ``total_words``, ``total_pangrams`` and ``highest_score`` fields.

The stats(``data/stats.json``) are then read by the socket-based stats server.

------

### 2. Socket-based stats application
**New folders:**\
``statsserver`` - TCP server that exposes stats over a socket
``statsclient`` - TCP client that queries the stats server

### Stats TCP server(``statsserver/main.go``)
- Listens on ``TCP port 6000``
- For each client connection (handled in a goroutine ``go handleConn(conn)``), it:
  - reads one-line commands
  - loads ``data/stats.json``
  - responds with the updated stats

**Supported commands:**
````
- TOTAL_GAMES -> Total games: x
- TOTAL_WORDS -> Total words: x
- TOTAL_PANGRAMS -> Total pangrams: x
- HIGHEST_SCORE -> Highest score: x
- QUIT -> Closes the connection
````

### Stats TCP client(``statsclient/main.go``)
- Connects to ``localhost:6000``
- Shows a ``>`` prompt to enter commands and displays the response.
- Starts a background goroutine to listen for RabbitMQ pangram events and prints them.

-----------

### 3. RabbitMQ pangram event queue
**New file:** ``server/rabbitmq.go`` 

- Uses ``github.com/streadway/amqp`` to connect to RabbitMQ
- Declares **fanout exchange** named ``pangram_events``

In SubmitWord(``server/service.go``):

- If a word is valid **AND** ``v.Pangram == true``, the server calls:
  - **publishPangramEvent**(``server/rabbitmq.go``)

The event message is a text string like, for example:
````
Pangram found! Game=GAME_ID, Word=PANGRAM_WORD, TotalScore=21
````

Consumer(``statsclient/main.go``) on startup calls startPangramListener which:
- Connects to RabbitMQ
- Declares the same ``pangram_events`` exchange fanout exchange
- Creates a temporary queue and binds it to the fanout exchange
- Starts a Goroutine to listen for messages on the temporary queue and display them.

Sample output in stats client:
````
[PANGRAM EVENT] Pangram found! Game=GAME_ID, Word=PANGRAM_WORD, TotalScore=21
````

**If multiple stats clients are connected, they will all receive the same pangram events broadcasted by the server.**

-----

### 4. Docker & Architecture
For Assignment 2, I decided to Dockerize:
- RabbitMQ 
- Spelling Bee gRPc server
- Stats TCP server

The clients(``client`` and ``statsclient``) are still run locally.

````
Dockerfile: Spelling Bee gRPC server(``server/Dockerfile``)
Dockerfile: Stats TCP server(``statsserver/Dockerfile``)
````

``docker-compose.yml`` orchestrates:
- ``rabitmq`` - ports 5672, 15672
- ``spellbee-server`` - port 50051
- ``stats-server`` - port 6000

The servers connect to RabbitMQ using the env var ``RABBITMQ_URL`` so they work both locally and in Docker.  

----

## Build / Run

The game can be run in two ways, either locally or in Docker.

### Option 1 - run locally:
```
Start RabbitMQ in cmd:
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=password rabbitmq:management

In GoLand terminal 1:
go run ./server

In GoLand terminal 2:
go run ./statsserver

In GoLand terminal 3:
go run ./client

In GoLand terminal 4:
go run ./statsclient
````
**Play until you get a pangram!**\
All connected stats clients will show a broadcast such as:
````
[PANGRAM EVENT] Pangram found! Game=GAME_ID, Word=PANGRAM_WORD, TotalScore=21
````


### Option 2 - run in Docker:

````
In GoLand terminal 1, from project root:
docker compose up --build

No need to run server/statsserver separately.

In GoLand terminal 2:
go run ./client

In GoLand terminal 3:
go run ./statsclient
````
**Play until you get a pangram!**\
All connected stats clients will receive the pangram event.
Example:
````
[PANGRAM EVENT] Pangram found! Game=GAME_ID, Word=PANGRAM_WORD, TotalScore=21
````

Stop containers with: ``docker compose down``

-------------

### Assignment 1:
- ``proto/spellbee.proto``
- ``server/main.go``, ``server/service.go``, ``server/rules.go``, ``server/dictionary.go``
- ``client/main.go``

### Assignment 2 additions:
- **Stats:** ``server/stats.go``, ``data/stats.json``
- **Socket app:** ``statsserver/main.go``, ``statsclient/main.go``
- **RabbitMQ:** ``server/rabbitmq.go``, startPangramListener(``statsclient/main.go``)
- **Docker:** ``server/Dockerfile``,``statsserver/Dockerfile``, ``docker-compose.yml``