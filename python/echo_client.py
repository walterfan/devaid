#!/usr/bin/python
# an example of echo client

import socket

def main():
    strIP = '127.0.0.1'
    nPort = 7676
    BUFFER_SIZE = 1024
    MESSAGE = "WalterTest"

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((strIP, nPort))

    while True:
        msg = raw_input('>')
        if not msg:
            break;
        sock.send(msg)
        data = sock.recv(BUFFER_SIZE)
        print "received data:", data
    sock.close()

if __name__ == '__main__':
    main()
