#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>

#include "send_packet.h"
#include "rdp.h"

/*
	Lager en forbindelse med en rdp server.

	args: sock - En socket som skal brukes til å sende og motta pakker
		  addr - En peker til en sockaddr med informasjon om serveren
		  addrlen - Lengden til adressen

	ret: En dynamisk allokert struct som inneholder data om en rdp forbindelse.
		 Returnerer NULL hvis det ikke ble laget en forbindelse på rikitg måte
*/
struct rdp_connection * rdp_connect(int sock, struct sockaddr_storage * addr, socklen_t addrlen) {

	//Kunne brukt et annet lettere seed til srand, for eksempel time(NULL) som ofte kommer opp på internett. 
	//Fant derimot ut at dette ga større variasjon i de tilfeldige tallene når man kjørte programmene med kort tidmellom.
	struct timeval temp;
	gettimeofday(&temp, NULL);
	srand((unsigned int)temp.tv_usec);

	struct rdp_packket * pakke = malloc(sizeof(struct rdp_packket));
	if (pakke == NULL) {
		perror("malloc");
		exit(EXIT_FAILURE);
	}

	struct rdp_connection * con = calloc(sizeof(struct rdp_connection), sizeof(char));
	if (con == NULL) {
		free(pakke);
		perror("malloc");
		exit(EXIT_FAILURE);
	}

	con->sockaddr = *addr;
	con->addr_len = addrlen;

	int forsok = 5;

	while(1) {
		con->senderid = rand() % 100000;
		pakke->senderid = con->senderid;
		pakke->flag = RDP_FLAG_CONREQ;
		pakke->pktseq = 0;
		pakke->ackseq = 0;
	
		pakke->recvid = 0;
		pakke->metadata = 0;
		pakke->payload = NULL;
		int rc = rdp_send(sock, pakke, con);
		if (rc == -1) {
			perror("rdp_send");
			freePakket(pakke);
			free(con);
			exit(EXIT_FAILURE);
		}

		fd_set set;
    	struct timeval timeout;

		FD_ZERO(&set);
		FD_SET(sock, &set);
		int sel;

		timeout.tv_sec = 1;
		timeout.tv_usec = 0;

		sel = select(FD_SETSIZE, &set, NULL, NULL, &timeout);

		if (sel > 0) {
			char buf[sizeof(struct rdp_packket)];
			rc = recv(sock, buf, sizeof(struct rdp_packket), 0);
			if (rc == -1) {
				perror("recv");
				freePakket(pakke);
				free(con);
				exit(EXIT_FAILURE);
			}

			rdp_unpack(buf, pakke);

			if (pakke->flag == RDP_FLAG_CONREF) {
				if (pakke->metadata > 0) {
					forsok--;
					printf("Trying again\n");
					if (forsok == 0) {
						printf("Cant get a valid connection with server\n");
						free(con);
						return NULL;
					} else {
						continue;
					}
				} else {
					printf("Server is full\n");
					freePakket(pakke);
					free(con);
					return NULL;
				}
				
			} else if (pakke->flag != RDP_FLAG_CONACK && pakke->flag != RDP_FLAG_DATA) {
				freePakket(pakke);
				free(con);
				fprintf(stderr, "feil flagg!\n");
				exit(EXIT_FAILURE);
			}
		
			con->recvid = pakke->senderid;
		
			freePakket(pakke);

			return con;
		} else {
			printf("Timeout, cant reach server\n");
			free(con);
			freePakket(pakke);
			return NULL;
		}
	}
}

/*
	Håndterer en forespørsel om forbindelse fra en klient

	args: sock - En socket som skal brukes til å sende og motta pakker
		  senderid - ID til klienten
		  mottat - En peker til pakken som serveren fikk fra klienten
		  addr - Informasjon om hvem som sendte forespørselen
		  addrlen - lengden adressen
		  cons - En liste med alle forbindelsene
		  antall - antall forbindelser som serveren har plass til

	ret: En dynamisk allokert struct som inneholder data om en rdp forbindelse.
		 Returnerer NULL hvis det ikke ble laget en forbindelse på rikitg måte.
*/
struct rdp_connection * rdp_accept(int sock, int senderid, struct rdp_packket *mottat, struct sockaddr_storage addr, socklen_t addrlen, struct rdp_connection ** cons, int antall) {
	struct rdp_packket * sendepakke = calloc(sizeof(struct rdp_packket), sizeof(char));
	if (sendepakke == NULL) {
		close(sock);
		perror("malloc");
		return NULL;
	}

	struct rdp_connection * con = malloc(sizeof(struct rdp_connection));
	if (con == NULL) {
		free(sendepakke);
		close(sock);
		perror("malloc");
		return NULL;
	}

	int erPlass = 0;
	int gyldigid = 1;
	for (int i = 0; i < antall; i++) {
		
		if (cons[i] == NULL) {
			erPlass = 1;

		} else if (mottat->senderid == cons[i]->recvid) {
			gyldigid = 0;
		}
	}

	con->sockaddr = addr;
	con->addr_len = addrlen;

	sendepakke->senderid = senderid;
	sendepakke->recvid = mottat->senderid;

	if (erPlass == 0) {
		sendepakke->flag = RDP_FLAG_CONREF;
		sendepakke->metadata = 0;
		printf("Sender pakke med -1\n");
		int rc = rdp_send(sock, sendepakke, con);
		if (rc == -1) {
			freePakket(sendepakke);
			free(con);
			close(sock);
			exit(EXIT_FAILURE);
		}
		freePakket(sendepakke);
		free(con);
		return NULL;
	}

	if (gyldigid == 0) {
		sendepakke->flag = RDP_FLAG_CONREF;
		sendepakke->metadata = 1;
		int rc = rdp_send(sock, sendepakke, con);
		if (rc == -1) {
			freePakket(sendepakke);
			free(con);
			close(sock);
			exit(EXIT_FAILURE);
		}
		freePakket(sendepakke);
		free(con);
		return NULL;
	}

	sendepakke->flag = RDP_FLAG_CONACK;
	int rc = rdp_send(sock, sendepakke, con);
	if (rc == -1) {
		freePakket(sendepakke);
		free(con);
		close(sock);
		exit(EXIT_FAILURE);
	}

	freePakket(sendepakke);

	
	con->senderid = senderid;
	con->recvid = mottat->senderid;
	con->pktseq = 0;
	con->ackseq = 0;

	return con;
}

/*
	Sender en pakke til en forbindlese

	args: sock - En socket som skal brukes til å sende og motta pakker.
		  pakke - Pakken som skal sendes.
		  con - Forbindelsen som skal brukes til å sende pakken

	ret: Antall bytes som ble sendt. Returnerer -1 dersom det har sjedd en feil og errno er satt.
*/
int rdp_send(int sock, struct rdp_packket * pakke, struct rdp_connection * con) {
	int index = 0;
	int rc;
	char * pack;
	if (pakke->flag == RDP_FLAG_DATA) {
		pack = malloc(sizeof(char)*pakke->metadata);
	} else {
		pack = malloc(sizeof(char)*(sizeof(struct rdp_packket))-8);
	}
	if (pack == NULL) {
		perror("malloc");
		return -1;
	}

	pack[index] = pakke->flag;
	index += sizeof(unsigned char);

	pack[index] = *(unsigned char *)&pakke->pktseq;
	index += sizeof(unsigned char);

	pack[index] = pakke->ackseq;
	index += sizeof(unsigned char);

	pack[index] = '\0';
	index += sizeof(char);

	*(int *)&pack[index] = htonl(pakke->senderid);
	index += sizeof(int);

	*(int *)&pack[index] = htonl(pakke->recvid);
	index += sizeof(int);

	*(int *)&pack[index] = htons(pakke->metadata);
	index += sizeof(int);

	if (pakke->flag == RDP_FLAG_DATA) {
		memcpy(&pack[index], pakke->payload, pakke->metadata - 16);
		rc = send_packet(sock, pack, pakke->metadata, 0, (struct sockaddr *)&con->sockaddr, sizeof(struct sockaddr));
	} else {
		rc = send_packet(sock, pack, sizeof(struct rdp_packket) - 8, 0, (struct sockaddr *)&con->sockaddr, sizeof(struct sockaddr));
	}

	free(pack);

	if (rc == -1) {
		perror("sendto");
		return -1;
	}

	int ret = gettimeofday(&con->siste_pakke, NULL);
	if (ret == -1) {
		perror("gettimeofday");
		return -1;
	}

	return rc;
}

/*
	Printer alle verdiene til pakken
*/
void printPakke(struct rdp_packket * pakke) {
	printf("Flag: %u\n", pakke->flag);
	printf("Pktseq: %u\n", pakke->pktseq);
	printf("Ackseq: %u\n", pakke->ackseq);
	printf("Senderid: %d\n", pakke->senderid);
	printf("Recvid: %d\n", pakke->recvid);
	printf("Metadata: %d\n", pakke->metadata);
	printf("Payload: %s\n", pakke->payload);
}

/*
	Fyller ut en pakke med data

	args: con - Forbindelsen som skal ha pakken.
		  flags - Hvilket flag pakken skal ha.
		  payload - Dataen som skal sendes.
		  metadata - Størrelsen til pakken.
		  pakke - Peker til pakken som skal fylles.
*/
void rdp_pack(struct rdp_connection * con, unsigned char flags, char* payload, int metadata, struct rdp_packket * pakke) {
	if (pakke->flag == RDP_FLAG_DATA && pakke->metadata > 16) {
		free(pakke->payload);
	}

	pakke->flag = flags;
	pakke->pktseq = *(unsigned long *)&con->pktseq;
	pakke->ackseq = con->ackseq;
	pakke->senderid = con->senderid;
	pakke->recvid = con->recvid;
	pakke->metadata = metadata + 16;

	if (flags == RDP_FLAG_DATA && metadata > 0) {
		pakke->payload = malloc(metadata);
		if (pakke->payload == NULL) {
			freePakket(pakke);
			exit(EXIT_FAILURE);
		}
		if (metadata > 0) {
			memcpy(pakke->payload, payload, sizeof(char)*metadata);
		}
	}
}

/*
	Fyller en pakke med data fra et buffer.

	args: buf - Buffer med dataen som ble sendt.
		  pakke - Peker til pakken som skal fylles.
*/
void rdp_unpack(char* buf, struct rdp_packket * pakke) {
	int offset = 0;

	if (pakke->flag == RDP_FLAG_DATA && pakke->metadata > 16) {
		free(pakke->payload);
	}

	pakke->flag = *(unsigned char *)&(buf[offset]);
	offset += sizeof(unsigned char);

	pakke->pktseq = *(unsigned char *)&(buf[offset]);
	offset += sizeof(unsigned char);

	pakke->ackseq = *(unsigned char *)&(buf[offset]);
	offset += sizeof(char);
	offset += sizeof(char);

	pakke->senderid = ntohl(*(int *)&buf[offset]);
	offset += sizeof(int);

	pakke->recvid = ntohl(*(int *)&buf[offset]);
	offset += sizeof(int);

	pakke->metadata = ntohs(*(int *)&buf[offset]);
	offset += sizeof(int);

	
	if (pakke->flag == RDP_FLAG_DATA && pakke->metadata > 16) {
		pakke->payload = malloc(sizeof(char)*(pakke->metadata)-16);
		if (pakke->payload == NULL) {
			perror("malloc");
			freePakket(pakke);
			exit(EXIT_FAILURE);
		}
		memcpy(pakke->payload, &buf[offset], pakke->metadata - 16);
	}
}

/*
	Frigjør minnet til en pakke.

	args: pakke - Peker til pakken som skal frigjøres
*/
void freePakket(struct rdp_packket * pakke) {
	if (pakke->flag == RDP_FLAG_DATA) {
		free(pakke->payload);
	}
	free(pakke);
}

/*
	Frigjør minne til alle forbindelsene. rpd_connection blir laget i enten rdp_accept eller rdp_connect. 
	Listen ligger i og blir allokert i enten serveren eller klienten

	args: cons - Liste med alle forbindelsene
		  antall - Antall forbindelser i listen
*/
void freeCons(struct rdp_connection ** cons, int antall) {
	for (int i = 0; i < antall; i++) {
		if (cons[i] != NULL) {
			free(cons[i]);
		}
		
	}

	free(cons);
}

