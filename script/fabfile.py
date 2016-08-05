from fabric.api import run
    
def freedisk(param='-h'):
    cmd = 'df ' + param
    run(cmd)

def hostinfo(param='-s'):
    cmd = 'uname ' + param
    run(cmd)

