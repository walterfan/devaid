import os,sys

host_file = 'C:/Windows/System32/drivers/etc/hosts'
host_name = 'waltersite'

def change_host(remote_host):
    
    content = ''
    print 'add host address in ', host_file
    # open hosts file as read mode
    input_fp = open(host_file, 'r')
    for line in input_fp:
        # find waltersite and remove the line
        if line.find(host_name) == -1:
            content += line 
    input_fp.close()

    # append a line host for the host_name
    content += remote_host + '\t' + host_name
    print content;
    
    output_fp = open(host_file, 'w')
    output_fp.write(content)
    output_fp.close()

if __name__ == "__main__":
    argc = len(sys.argv)
    remote_host = '192.168.3.14'
    if( argc > 1):
        remote_host = sys.argv[1]
        change_host(remote_host)
    else:
        print "Usage: python {0} <remote_host> # it's {1} by default ".format(sys.argv[0], remote_host)
        change_host(remote_host)