#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/select.h>
#include <sys/time.h>

#include "rdp.h"
#include "send_packet.h"

#define BUFSIZE 1000

//Insiprasjon fra Cbra
void check_error(int ret, char *msg, int sock, struct rdp_packket * pakke, struct rdp_connection ** cons, int antall) {
	if (ret == -1) {
		perror(msg);
		close(sock);
		freeCons(cons, antall);
		freePakket(pakke);
		exit(EXIT_FAILURE);
	}
}

/*
	Fyller et buffer opp med data fra riktig sted i en fil

	args: buf - Bufferet som skal fylles opp
		  fil - Files som skal leses
		  index - Plasseringen som skal leses fra

	ret: Antall bytes som ble lest
*/
int readNextFilBuf(char *buf, FILE *fil, int index) {
	int rc = fseek(fil, BUFSIZE * index, SEEK_SET);
	if (rc == -1) {
		perror("fseek");
		fclose(fil);
		exit(EXIT_FAILURE);
	}

	rc = fread(buf, 1, BUFSIZE, fil);
	if (rc == -1) {
		perror("fread");
		fclose(fil);
		exit(EXIT_FAILURE);
	}

	return rc;
}

/*
	Henter en forbindelse fra en liste basert på en id

	args: cons - Liste med forbindelser
		  antall - Størrelsen på listen
		  id - ID til klienten

	ret: Returnerer forbindelsen med riktig ID, eller NULL dersom IDen ikke finnes i listen
*/
struct rdp_connection * getConnection(struct rdp_connection ** cons, int antall, int id) {
	for (int i = 0; i < antall; i++) {
		if (cons[i] != NULL && cons[i]->recvid == id && cons[i]->senderid != -1) {
			return cons[i];
		}
	}
	return NULL;
}

//Henter en indeks hvor det er plass i listen til en ny forbindelse
int getFreeCon(struct rdp_connection ** cons, int antall) {
	for (int i = 0; i < antall; i++) {
		if (cons[i] == NULL) {
			return i;
		}
	}
	return -1;
}

/*
	Metode for å finne ut om alle forbindelsene er ferdige
	Returns 0 hvis alle er ferdige, returnerer 1 hvis det fortsatt er flere forbindelser eller plasser igjen.
*/
int erFerdig(struct rdp_connection ** cons, int antall) {
	for (int i = 0; i < antall; i++) {
		if (cons[i] == NULL) {
			return 1;
		} else if (cons[i]->senderid != -1) {
			return 1;
		}
	}

	return 0;
}

int main(int argc, char *argv[]) {
	if (argc != 5) {
		printf("Usage: %s <Port> <Filename> <Num of files> <Prob>", argv[0]);
		return EXIT_FAILURE;
	}

	const int port = (int)strtol(argv[1], NULL, 10);
	const int serverid = 1111;
	const float prob = strtof(argv[4], NULL);
	const int antall = (int)strtol(argv[3], NULL, 10);

	FILE *fil = fopen(argv[2], "r");
	if (fil == NULL) {
		perror("fopen");
		exit(EXIT_SUCCESS);
	}

	//Sjekker om parameterene har riktig format
	if (antall < 1 || port < 1024 || port > 65535 || prob < 0.0f || prob > 1.0f) {
		printf("Usage: %s <Port> <Filename> <Num of files> <Prob>\n", argv[0]);
		fclose(fil);
		return EXIT_SUCCESS;
	}

	set_loss_probability(prob);

	int sock, rc, fc;
	char buf[BUFSIZE];
	struct sockaddr_in adresse;

	int ready = 1;

	struct sockaddr_storage peer_addr;
    socklen_t peer_addr_len;


    struct rdp_connection ** connections = calloc(antall, sizeof(struct rdp_connection *));
    if (connections == NULL) {
    	perror("calloc");
    	fclose(fil);
    	exit(EXIT_FAILURE);
    }


	//Setter typen til serveren
	adresse.sin_family = AF_INET;

	//Setter porten til serveren
	adresse.sin_port = htons(port);

	//Setter serveren til å høre på alle adresser
	adresse.sin_addr.s_addr = INADDR_ANY;

	char filBuf[BUFSIZE];

	sock = socket(AF_INET, SOCK_DGRAM, 0);
	if (sock == -1) {
		perror("Socket");
		fclose(fil);
		free(connections);
		exit(EXIT_FAILURE);
	}

	rc = 1;
	rc = setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &rc, sizeof(rc));
	if (rc == -1) {
		perror("setsockopt");
		fclose(fil);
		free(connections);
		close(sock);
		exit(EXIT_FAILURE);
	}

	rc = bind(sock, (struct sockaddr *)&adresse, sizeof(struct sockaddr_in));
	if (rc == -1) {
		perror("bind");
		close(sock);
		fclose(fil);
		free(connections);
		exit(EXIT_FAILURE);
	}

	struct rdp_packket * pakke = calloc(sizeof(struct rdp_packket), sizeof(char));
	if (pakke == NULL) {
		perror("calloc");
		close(sock);
		fclose(fil);
		free(connections);
		exit(EXIT_FAILURE);
	}

	printf("Klar til aa begynne\n");

	//Hendelseslokken
	while(1) {

		fd_set set;
    	struct timeval timeout;

		FD_ZERO(&set);
		FD_SET(sock, &set);
		int sel;

		//Dersom ingen klienter har koblet seg til vil select ikke ha timeout. 
		//Når den første klienten har koblet seg til, vil select ha en timeout på 500 mikrosekunder.
		//Gikk for denne løsningen fordi det gjør at serveren i hvert fall kan vente til første klient har koblet seg 
		//på før den kan begynne å med timeout.
		if (ready == 1) {
			sel = select(FD_SETSIZE, &set, NULL, NULL, NULL);
		} else {
			timeout.tv_sec = 0;
			timeout.tv_usec = 500;
			sel = select(FD_SETSIZE, &set, NULL, NULL, &timeout);
		}

		check_error(sel, "select", sock, pakke, connections, antall);
	
		//Siden det bare en en socket, trenger ikke koden sjekke hvilken socket som hatt en hendelse
		if (sel > 0) {
			//Mottar en melding på en socket
			rc = recvfrom(sock, buf, BUFSIZE - 1, 0, (struct sockaddr *)&peer_addr, &peer_addr_len);
			check_error(rc, "recvfrom", sock, pakke, connections, antall);
		
			rdp_unpack(buf, pakke);	//finner dataen i meldingen
			struct rdp_connection * client = getConnection(connections, antall, pakke->senderid); //Finner forbindelsen som sendte meldingen

			//Pakken er ikke en forespørsel og forbindelsen finnes ikke fra før av. Kaster pakken.
			if (client == NULL && pakke->flag != RDP_FLAG_CONREQ) {
				printf("Mottok en unidentifiserbar pakke\n");
				continue;
			}

			
			if (pakke->flag == RDP_FLAG_CONREQ) { //Pakken er en forbindelse foresørsel
				printf("Connecting...\n");
				client = rdp_accept(sock, serverid, pakke, peer_addr, peer_addr_len, connections, antall);
				if (client == NULL) {
					printf("Connection failed\n");
				} else {
					printf("Connection completed\n");
					
					//Legger til en forbindelse på en ledig plass i listen
					int i = getFreeCon(connections, antall); 
					if (i == -1) {
						fprintf(stderr, "Vi har et problem, det er ikke plass likevel\n"); //Da har det skjedd en feil
						close(sock);
						freeCons(connections, antall);
						freePakket(pakke);
						exit(EXIT_FAILURE);
					} else {
						connections[i] = client;
						printf("Klienten er lagt til på indeks %d\n", i);
					}

					ready = 0;
					printf("CONNCTED %d %d\n", client->recvid, client->senderid);
	
					fc = readNextFilBuf(filBuf, fil, client->pktseq); //Leser neste pakke inn i bufferet
					printf("Sender pakke nr %lu til klient %d\n", client->pktseq, client->recvid);

					if (fc > 0) { //Klienten har ikke faatt hele filen
						rdp_pack(client, RDP_FLAG_DATA, filBuf, fc, pakke);
						rc = rdp_send(sock, pakke, client);
						check_error(rc, "rdp_send", sock, pakke, connections, antall);
					} else { //Kienten har mottat hele filen
						rdp_pack(client, RDP_FLAG_DATA, 0, 0, pakke);
						rc = rdp_send(sock, pakke, client);
						check_error(rc, "rdp_send", sock, pakke, connections, antall);
					}
				}
	
			//Pakken som er mettat er en ACK
			} else if (pakke->flag == RDP_FLAG_ACK && pakke->senderid == client->recvid && *(unsigned char *)&client->pktseq == pakke->ackseq) {
				client->pktseq = client->pktseq + 1;
				printf("Sender pakke nr %lu til klient %d\n", client->pktseq, client->recvid);

				fc = readNextFilBuf(filBuf, fil, client->pktseq); //Leser neste
				if (fc > 0) {
					rdp_pack(client, RDP_FLAG_DATA, filBuf, fc, pakke);
					rc = rdp_send(sock, pakke, client);
					check_error(rc, "rdp_send", sock, pakke, connections, antall);
				} else {
					rdp_pack(client, RDP_FLAG_DATA, 0, 0, pakke);
					rc = rdp_send(sock, pakke, client);
					check_error(rc, "rdp_send", sock, pakke, connections, antall);
				}
	
			//Pakken er en forbindelsesavslutning
			} else if (pakke->flag == RDP_FLAG_CONTERM) {
				
				printf("DISCONNECTED %d %d\n", client->recvid, client->senderid);
				client->senderid = -1;
				if (erFerdig(connections, antall) == 0) { //Programmet er ferdig med alle klientene
					printf("ferdig!!\n");
					freePakket(pakke);
					freeCons(connections, antall);
					close(sock);
					fclose(fil);
					return EXIT_SUCCESS;
				}			
			}
		}

		struct timeval tid;
		if (gettimeofday(&tid, NULL) == -1) {
			perror("gettimeofday");
			exit(EXIT_FAILURE);
		}

		//Går igjennom alle forbindelsene og ser om en pakke har blitt tapt, sender isåfall en ny pakke.
		for (int i = 0; i < antall; i++) {
			if (connections[i] != NULL && connections[i]->senderid != -1) {
				int forskjell = ((tid.tv_sec - connections[i]->siste_pakke.tv_sec) * 1000) + ((tid.tv_usec - connections[i]->siste_pakke.tv_usec) / 1000);
				if (forskjell > 100) {
					printf("Har ventet i %d, sender en ny pakke nr %lu\n", forskjell, connections[i]->pktseq);
					fc = readNextFilBuf(filBuf, fil, connections[i]->pktseq);
					rdp_pack(connections[i], RDP_FLAG_DATA, filBuf, fc, pakke);
					rc = rdp_send(sock, pakke, connections[i]);
					check_error(rc, "rdp_send", sock, pakke, connections, antall);
				}
			}
		}
	}
}

















