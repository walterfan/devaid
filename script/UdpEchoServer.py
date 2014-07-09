#!/usr/bin/env python

import socket, traceback, sys

host = '127.0.0.1'
port = 5062
if(len(sys.argv) > 1):
    port = sys.argv[1]


s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind((host, port))
print "listen on port ", port
while 1:
    try:
        message, address = s.recvfrom(8192)
        print "Got data from", address, ": ", message
        s.sendto(message, address)
        if(message == "bye"):
            print "\nok, byebye\n"
            break
    except (KeyboardInterrupt, SystemExit):
        raise
    except:
        traceback.print_exc()
