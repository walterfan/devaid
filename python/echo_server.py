#!/usr/bin/python
# an example of echo server

import socket

def main():
    strIP = '127.0.0.1'
    nPort = 7676
    BUFFER_SIZE = 20  # Normally 1024, but we want fast response

    svr = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    svr.bind((strIP, nPort))
    svr.listen(5)
    
    while True:
        print 'wait connection...'
        conn, addr = svr.accept()
        print 'Connected from:', addr
        while True:
            data = conn.recv(BUFFER_SIZE)
            if not data: 
                break
            print "received data:", data
            conn.send(data)  # echo
        conn.close()
    svr.close()

if __name__ == '__main__':
    main()
