#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/time.h>
#include <string.h>
#include <time.h>
#include <sys/stat.h>

#include "rdp.h"
#include "send_packet.h"

#define PORT 1234
#define BUFSIZE 1016

//Insiprasjon fra Cbra
void check_error(int ret, char *msg, int sock, FILE *fil, struct rdp_connection *con) {
	if (ret == -1) {
		perror(msg);
		close(sock);
		fclose(fil);
		free(con);
		exit(EXIT_FAILURE);
	}
}


int main(int argc, char *argv[]) {

	if (argc != 4) {
		printf("Usage: %s <IP adress> <Port> <probability>", argv[0]);
		return EXIT_SUCCESS;
	}

	const int port = (int)strtol(argv[2], NULL, 10);
	const float prob = strtof(argv[3], NULL);

	if (port < 1024 || port > 65535 || prob < 0.0f || prob > 1.0f) {
		printf("Usage: %s <IP adress> <Port> <Prob>\n", argv[0]);
		return EXIT_SUCCESS;
	}

	set_loss_probability(prob);

	int sock, rc;
	struct in_addr ipadresse;
	struct sockaddr_in adresse;

	rc = inet_pton(AF_INET, argv[1], &ipadresse);

	//Setter typen server til IPv4
	adresse.sin_family = AF_INET;

	//Setter porten til serveren
	adresse.sin_port = htons(PORT);

	//Setter ipadressen til serveren
	adresse.sin_addr = ipadresse;


		
	char filname[21];
	srand(time(NULL));
	while(1) {
		sprintf(filname, "kernel-file-%d.txt", rand() % 10000);
		
		struct stat filstat;
		int ret = stat(filname, &filstat);
		if (ret == -1) {
			break;
		}
	}

	sock = socket(AF_INET, SOCK_DGRAM, 0);
	if (sock == -1) {
		exit(EXIT_FAILURE);
	}


	struct rdp_packket pakke;
	pakke.flag = 0;

	printf("Connecting...\n");
	struct rdp_connection * server = rdp_connect(sock, (struct sockaddr_storage *)&adresse, sizeof(struct sockaddr));
	if (server == NULL) {
		fprintf(stderr, "Connection failed\n");
		close(sock);
		free(server);
		exit(EXIT_SUCCESS);
	} else {
		printf("Connection completed\n");
		printf("CONNCTED %d %d\n", server->senderid, server->recvid);
	}


	FILE *fil = fopen(filname, "w");
	if (fil == NULL) {
		perror("fopen");
		close(sock);
		free(server);
		exit(EXIT_FAILURE);
	}

	while(1) {
		char buf[BUFSIZE];
		rc = recv(sock, buf, BUFSIZE, 0);
		check_error(rc, "recv", sock, fil, server);

		rdp_unpack(buf, &pakke);

		if (pakke.recvid != server->senderid && pakke.senderid != server->recvid) {
			printf("Mottok en unidentifiserbar pakke\n");
			continue;
		}

		if (pakke.flag == RDP_FLAG_DATA && pakke.metadata == 16) {
			printf("Filen er skrevet til: %s\n", filname);
			rdp_pack(server, RDP_FLAG_CONTERM, NULL, 0, &pakke);
			rdp_send(sock, &pakke, server);
			printf("DISCONNECTED %d %d\n", server->senderid, server->recvid);
			close(sock);
			free(server);
			fclose(fil);
			return EXIT_SUCCESS;
		} else if (pakke.flag == RDP_FLAG_DATA && (pakke.pktseq == server->pktseq + 1 || pakke.pktseq == 0)) {
			int wc = fwrite(pakke.payload, sizeof(char), pakke.metadata - 16, fil);
			if (wc != pakke.metadata - 16) {
				fprintf(stderr, "Det har skjedd noe med pakken!\n");
				close(sock);
				free(server);
				fclose(fil);
				exit(EXIT_FAILURE);
			}

			server->pktseq = pakke.pktseq;
			server->ackseq = pakke.pktseq;					

		}
		printf("Sender en ACK nr %d\n", server->ackseq);
		rdp_pack(server, RDP_FLAG_ACK, NULL, 0, &pakke);
		rc = rdp_send(sock, &pakke, server);
		check_error(rc, "recv", sock, fil, server);
	}
}





