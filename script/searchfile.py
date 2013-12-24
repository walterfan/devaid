import os
import re
import sys
import shutil
from os.path import join, getsize
from optparse import OptionParser

def delete_tree(topdir):
    for root, dirs, files in os.walk(topdir, topdown=False):
        for name in files:
            os.remove(os.path.join(root, name))
        for name in dirs:
            os.rmdir(os.path.join(root, name))

def print_file(filepath):
    if os.path.isdir(filepath):
        print(filepath + " directory match")
    else:
        print(filepath + " match")
    
def delete_file(filepath):    
    if os.path.isdir(filepath):
        #delete_tree(filepath)
        shutil.rmtree(filepath)
        print(filepath + " directory removed")
    else:
        os.remove(filepath)
        print(filepath + " removed")
    

def search_file(fileexp='.+', subdir='', filehandler = print_file):
    if subdir:
        path = subdir
    else:
        path = os.getcwd()
        
    regex = re.compile(fileexp)
        
    for root, dirs, names in os.walk(path):

        for dirname in dirs:
            result = regex.match(dirname)
            if result:
                filehandler(os.path.join(root, dirname))
        for filename in names:
            #print(os.path.join(root, filename))
            result = regex.match(filename)    
            if result:
                filehandler(os.path.join(root, filename))

if __name__ == "__main__":
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