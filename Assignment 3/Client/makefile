CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread

all: BBClient
	g++ -o bin/BBClient bin/Task.o bin/connectionHandler.o bin/BBClient.o $(LDFLAGS) 
	@echo "Build success"

BBClient: bin/connectionHandler.o bin/BBClient.o  bin/Task.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BBClient.o: src/BBClient.cpp
	g++ $(CFLAGS) -o bin/BBClient.o src/BBClient.cpp

bin/Task.o: src/Task.cpp
	g++ $(CFLAGS) -o bin/Task.o src/Task.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
