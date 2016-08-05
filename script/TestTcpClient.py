#!/usr/bin/env python

import socket
import sys

host = "10.224.57.202"
nPort = 12345

if(len(sys.argv) > 1):
    host = sys.argv[1]
if(len(sys.argv) > 2):
    nPort = int(sys.argv[2])

BUFFER_SIZE = 1500
MESSAGE = "<DS10>cmd=AuthReq&ip=10.224.64.46&appName=APP_IVR&appToken=123456789&seq=123</DS10>"

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((host, nPort))
s.send(MESSAGE)
data = s.recv(BUFFER_SIZE)
s.close()

print "received data:", data

