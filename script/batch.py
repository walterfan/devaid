'''
    like bat file, execute the steps in batch.json 
'''
import os,sys,subprocess
import time,thread
import codecs
import json
from datetime import datetime
from subprocess import call
from pprint import pprint

def execute_json(json_file):
    print "# Execute {0} begin\n---------------------------".format(json_file)

    json_data=open(json_file)
    data = json.load(json_data)
    cnt = len(data['steps'])
    i = 0
    print "\n## Will execute {0} steps \n~~~~~~~~~~~~~~~~~~~~~~~~~~~".format(cnt)
    for i in range(0, cnt):
            print "{0}. [{1}]: {2}".format(i, data['steps'][i]['step'], data['steps'][i]['desc'])

    #pprint(data)
    #print("cnt=", cnt)
    for i in range(0, cnt):
        cmd = data['steps'][i]['step']
        desc = data['steps'][i]['desc']
        print "\n* {0}. [{1}]: {2} ".format(i, cmd, desc)
        
        if(cmd.startswith('cd')):
            cmd = cmd.replace("cd ", "")
            os.chdir(cmd)
        else:
            ret = os.system(cmd)
            if(ret != 0):
                print "Encounter error of step {0}. {1}, error code={2}".format(i, cmd, ret)
                break

    print "\n## Done the following steps\n~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    for j in range(0, i):
        print "{0}. [{1}]: {2}".format(j, data['steps'][j]['step'], data['steps'][j]['desc'])    
    json_data.close()
    print "# Execute {0} end.".format(json_file)

if __name__ == "__main__":
    argc = len(sys.argv)
    step_file = 'batch.json'
    if( argc > 1):
        idx = 1
        while(idx < argc):
            step_file = sys.argv[idx]
            execute_json(step_file)
            idx = idx + 1
    else:
        print "Usage: python {0} <batch_json_file>".format(sys.argv[0])
        print "note: execute the batch.json by default"
        execute_json(step_file)

