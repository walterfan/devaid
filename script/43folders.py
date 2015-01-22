import os,sys,subprocess
import time,thread
import codecs
import json
from datetime import datetime
from subprocess import call
from pprint import pprint

months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 
          'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']


def create_folder(folder_name):
    
    if not os.path.exists(path):
        os.mkdir(path)
        
class Folders43(object):
    _root = ''
    _days = []
    
    def __init__(self, root_path):
        self._root = root_path

    def create_folder(self, year):
    #create months and days for the year
        for mon in months:
            print 'add',year, mon
        for day in range(1, 32):
            print 'add', year, day
            
    def move_folder(self):
        #move days to last month
        print "move folder"
        
if __name__ == "__main__":
    argc = len(sys.argv)
    print "\tcreate 43 folders for one year, archive monthly if created"
    obj = Folders43('.')
    obj.create_folder('2015')
    

