#include <unistd.h>
#include <arpa/inet.h>
#include <sys/time.h>

//Flaggene til pakkene
#define RDP_FLAG_CONREQ 0x01
#define RDP_FLAG_CONTERM 0x02
#define RDP_FLAG_DATA 0x04
#define RDP_FLAG_ACK 0x08
#define RDP_FLAG_CONACK 0x10
#define RDP_FLAG_CONREF 0x20

//Struct som inneholder data til forbindelsene
struct rdp_connection {
	int senderid;
	int recvid;
	struct sockaddr_storage sockaddr;
	socklen_t addr_len;
	unsigned long pktseq;
	unsigned char ackseq;
	struct timeval siste_pakke;
};

//Struct som inneholder data til pakkene
struct rdp_packket {
	unsigned char flag;
	unsigned char pktseq;
	unsigned char ackseq;
	int senderid;
	int recvid;
	int metadata;
	char* payload;
};

struct rdp_connection * rdp_accept(int sock, int senderid, struct rdp_packket *mottat, struct sockaddr_storage addr, socklen_t addrlen, struct rdp_connection ** cons, int antall);

struct rdp_connection * rdp_connect(int sock, struct sockaddr_storage *addr, socklen_t addrlen);

int rdp_send(int sock, struct rdp_packket * pakke, struct rdp_connection * con);

void rdp_pack(struct rdp_connection * con, unsigned char flags, char* payload, int metadata, struct rdp_packket * pakke);

void rdp_unpack(char* buf, struct rdp_packket * pakke);

void freePakket(struct rdp_packket * pakket);

void printPakke(struct rdp_packket * pakke);

void freeCons(struct rdp_connection ** cons, int antall);



















