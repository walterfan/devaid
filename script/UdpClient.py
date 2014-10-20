#!/usr/bin/env python

import socket, sys

host = "127.0.0.1"
nPort = 5062

if(len(sys.argv) > 1):
    host = sys.argv[1]
if(len(sys.argv) > 2):
    nPort = sys.argv[2]

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
try:
    port = int(nPort)
except ValueError:
    port = socket.getservbyname(nPort, 'udp')
s.connect((host, port))
while 1:
    print "Enter data to transmit:"
    data = sys.stdin.readline().strip()
    s.sendall(data)
    print "input bye to exit"
    buf = s.recv(2048)
    if not len(buf):
        break
    print "Server replies: ",
    sys.stdout.write(buf)
    if(data == "bye" or buf == "bye"):
        print "\nok, byebye\n"
        break

    print "\n"
