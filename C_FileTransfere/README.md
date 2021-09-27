# File transfere with multiplexing and loss recovery
Part one of the exam in IN2140 - Introduction to operating systems and data communication. This is a server-client program that sends files from the server to a client over iternet. It uses socket and the UDP protocol to send the file.

### Requirements
- C
- gcc for compiling

### Functionality
- The client connects to a server and get a file from the server
- The server-client supports multiplexing, this means that multiple clients can connect to the server at one time
- The server uses UDP, but still supports loss recovery
- The server will only send a limited number of files

### Limitations
- The maximum size of the files are 4294967295000 bytes (around 3.9 terbytes)
- The server can mximum serve 100000 clients in one running

### Usage
Use the makefile to compile
```bash
make all
```
To run the server use
```bash
./server <port> <filename> <number of clients> <packetloss>
```
Where the packeloss is a number between 0 and 1. this is a amount of loss the data should have.

To run the client use
```bash
./client <IP adress> <port> <packetloss>
```

It is also possible to use the makefile to run the programs. Then you need to change the IP adress on line 27.

To run the server with make file, run
```bash
make runS
```

To run the client with make file, run
```bash
make runC
```

