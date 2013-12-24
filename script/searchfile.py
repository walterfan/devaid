import os
import re
import sys
from os.path import join, getsize
from optparse import OptionParser

def print_file(filepath):
    print(filepath + " match")
    
def delete_file(filepath):    
    os.remove(filepath)
    print(filepath + " removed")

def search_file(fileexp='.+', subdir='', filehandler = print_file):
    if subdir:
        path = subdir
    else:
        path = os.getcwd()
    for root, dirs, names in os.walk(path):
        for filename in names:
            #print(os.path.join(root, filename))
            regex = re.compile(fileexp)
            result = regex.match(filename)
            
            if result:
                filehandler(os.path.join(root, filename))

usage = "usage: %prog [options] <regular_expression>"
parser = OptionParser(usage=usage)
parser.add_option("-p", "--path", dest="path", help="specify the search path, it's . by default", metavar="<path>",)
parser.add_option("-l", "--list", help="search and print file", action="store_false")
parser.add_option("-d", "--delete", help="search and delete file", action="store_false")

(options, args) = parser.parse_args()

argc = len(args)
if(argc == 0):
    parser.print_help()
else:
    thepath = ''
    if(None != options.path):
        thepath = options.path
    print("* the regular expression is " + args[0])
    if(None != options.delete):
        search_file(args[0], thepath, delete_file)
    else:        
        search_file(args[0], thepath, print_file)