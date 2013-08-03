#include <stdio.h>

#define LITTLE_ENDIAN 0
#define BIG_ENDIAN    1

int endian() {
    int i = 1;
    char *p = (char *)&i;

    if (p[0] == 1)
        return LITTLE_ENDIAN;
    else
        return BIG_ENDIAN;
}


int main(int argc, char **argv)
{
    union {
        short s;
        char c[sizeof(short)];
    }un;
    un.s = 0x0102;

    if (sizeof(short) == 2) {
        if (un.c[0] == 1 && un.c[1] == 2)
            printf("big-endian\n");
        else if (un.c[0] == 2 && un.c[1] == 1)
            printf("little-endian\n");
        else
            printf("unknown\n");
   } else{
        printf("sizeof(short) = %d\n", sizeof(short));
   }
   exit(0);
}

