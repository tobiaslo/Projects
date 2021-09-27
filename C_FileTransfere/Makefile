FLAGS=-g -Wall -Wextra -std=gnu11
BIN=server client *.o kernel-file*

.PHONY : all clean

all: server client

server: server.o rdp.o send_packet.o
	gcc $(FLAGS) server.o rdp.o send_packet.o -o server

client: client.o rdp.o send_packet.o
	gcc $(FLAGS) client.o rdp.o send_packet.o -o client

server.o: server.c rdp.h
	gcc -c $(FLAGS) server.c

client.o: client.c rdp.h
	gcc -c $(FLAGS) client.c

rdp.o: rdp.c rdp.h
	gcc -c $(FLAGS) rdp.c

send_packet.o: send_packet.c send_packet.h 
	gcc -c $(FLAGS) send_packet.c

runC: all
	./client 127.0.0.1 1234 0.3

runS: all
	./server 1234 "testfil.txt" 3 0.3

valgrindS: all
	valgrind --leak-check=full ./server 1234 "testfil.txt" 1 0.2

valgrindC: all
	valgrind --leak-check=full ./client 127.0.0.1 1234 0.2

clean:
	rm -f $(BIN)