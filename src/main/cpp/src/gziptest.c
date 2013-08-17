/* This code may be used by anyone. Errors and improvements should be reported to the author */
/* Tested with gzip 1.2.1 and gzip 1.2.2 */
#include <zlib.h>
#include <malloc.h>
#include <stdio.h>

/** Buffersize for compress/decompress **/
#define INCREASE 10240
#define uint32_t unsigned int
#ifndef max
#define max(x,y) ((x) > (y) ? (x) : (y))
#endif

extern char *base64(const unsigned char *input, int length);

/** Compress data by GZIP in Memory

    @param source Pointer to the data to be compressed
    @param sourcelen len of sourcedata
    @param target Pointer to the result. Has to be freed by 'free'
    @param targetlen Len of targetdata
    @return always 0
**/
int Compress(const unsigned char *source, unsigned int sourcelen, unsigned char** target, unsigned int *targetlen)
{
    z_stream c_stream;
    memset(&c_stream, 0, sizeof(c_stream));

    int ret = 0;
    int err;
    int alloclen = max(sourcelen, INCREASE);

    c_stream.zalloc = NULL;
    c_stream.zfree = NULL;
    c_stream.opaque = NULL;

    *target = (unsigned char *) malloc(alloclen);

    // Initialisation, so that GZIP will be created
    if (deflateInit2(&c_stream,Z_BEST_COMPRESSION,Z_DEFLATED, 15 + 16, 8, Z_DEFAULT_STRATEGY) == Z_OK)
    {
        c_stream.next_in  = (Bytef*)source;
        c_stream.avail_in = sourcelen;
        c_stream.next_out = *target;
        c_stream.avail_out = alloclen;

        while (c_stream.total_in != sourcelen && c_stream.total_out < *targetlen)
        {
            err = deflate(&c_stream, Z_NO_FLUSH);
            // CHECK_ERR(err, "deflate");
            if (c_stream.avail_out == 0)
            {
                // Alloc new memory
                int now = alloclen;
                alloclen += alloclen / 10 + INCREASE;
                *target = (unsigned char *) realloc(*target, alloclen);
                c_stream.next_out = *target + now;
                c_stream.avail_out = alloclen - now;
            }
        }
        // Finish the stream
        for (;;)
        {
            err = deflate(&c_stream, Z_FINISH);
            if (err == Z_STREAM_END) break;
            if (c_stream.avail_out == 0)
            {
                // Alloc new memory
                int now = alloclen;
                alloclen += alloclen / 10 + INCREASE;
                *target = (unsigned char *) realloc(*target, alloclen);
                c_stream.next_out = *target + now;
                c_stream.avail_out = alloclen - now;
            }
            // CHECK_ERR(err, "deflate");
        }

        err = deflateEnd(&c_stream);
        // CHECK_ERR(err, "deflateEnd");
    }
    *targetlen = c_stream.total_out;
    // free remaining memory
    *target = (unsigned char *) realloc(*target, *targetlen);

    return ret;
}

/** Inflate data with GZIP

    @param source Pointer to the compressed data
    @param sourcelen Len of compressed data
    @param target Pointer to the inflated data, has to be freed with 'free'
    @param targetlen Len of inflated data
    @return always 0
**/
int Decompress(const unsigned char *source, unsigned int sourcelen, unsigned char **target, unsigned int *targetlen)
{
    z_stream c_stream;
    memset(&c_stream, 0, sizeof(c_stream));

    int ret = 0;
    int err;
    int alloclen = max(sourcelen * 2, INCREASE);

    c_stream.zalloc = NULL;
    c_stream.zfree = NULL;
    c_stream.opaque = NULL;

    *target = (unsigned char *) malloc(alloclen+1);
    *targetlen = 0;

    if (inflateInit2(&c_stream, 15 + 16) == Z_OK)
    {
        c_stream.next_in  = (Bytef*)source;
        c_stream.avail_in = sourcelen;
        c_stream.next_out = *target;
        c_stream.avail_out = alloclen;

        while (c_stream.total_in != sourcelen && c_stream.total_out < *targetlen)
        {
            err = inflate(&c_stream, Z_NO_FLUSH);
            // CHECK_ERR(err, "deflate");
            if (c_stream.avail_out == 0)
            {
                // Alloc new memory
                int now = alloclen;
                alloclen += alloclen / 10 + INCREASE;
                *target = (unsigned char *) realloc(*target, alloclen+1);
                c_stream.next_out = *target + now;
                c_stream.avail_out = alloclen - now;
            }
        }
        // Finish the stream
        for (;;)
        {
            err = inflate(&c_stream, Z_FINISH);
            if (err == Z_STREAM_END) break;
            if (c_stream.avail_out == 0)
            {
                // alloc new memory
                int now = alloclen;
                alloclen += alloclen / 10 + INCREASE;
                *target = (unsigned char *) realloc(*target, alloclen+1);
                c_stream.next_out = *target + now;
                c_stream.avail_out = alloclen - now;
            }
            // CHECK_ERR(err, "deflate");
        }

        err = inflateEnd(&c_stream);
        // CHECK_ERR(err, "deflateEnd");
    }
    *targetlen = c_stream.total_out;
    // Free remaining memory
    *target = (unsigned char *) realloc(*target, *targetlen);

	 return 0;
}

int main(int argc, char *argv[])
{
	//unsigned char* szSrc= "hello world";
	unsigned char* szDest = NULL;
	char input[12] = {0x68,0x65,0x6C,0x6C,0x6F,0x20,0x77,0x6F,0x72,0x6C,0x64,'\0'};
	unsigned int nDest = 0;

	printf("base64(gzip(%s))\n", input);
	Compress(input, strlen(input), &szDest, &nDest);
	printf("from %d to %d\n", strlen(input), nDest);

	//char szLen[2] = {0x0B, '\0'};
	//char* lenOut = base64(szLen, strlen(szLen));
	//printf("Base64: %s\n", lenOut);
	//free(lenOut);

	char* output = base64(szDest, nDest);
	printf("Base64: %s\n", output);
	free(output);
}

