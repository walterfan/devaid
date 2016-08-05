# read file and process line text one by one
from __future__ import print_function
import sys

def print_usage():
	usage = "usage: %prog <file_path>"
	print(usage)

def read_file(file_path):
    fp = open(file_path)
    print("read ",file_path)
    try:
    	for line in fp:
            print("* RFC ",line, end="\n")
    finally:
    	fp.close()


if __name__ == "__main__":

	argc = len(sys.argv)
	
	if(argc < 2):
		print_usage()
	else:
		read_file(sys.argv[1])

