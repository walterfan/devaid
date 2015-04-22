from twisted.internet import protocol, reactor
from time import ctime

HOST = '127.0.0.1'
PORT = 21567

class CTimerClient(protocol.Protocol):
    def connectionMade(self):
        self.sendData()
        
    def sendData(self):
        data = raw_input('message> ')
        if data:
            print 'send %s ...' % data
            self.transport.write(data)
        else:
            self.transport.loseConnection()
        
    def dataReceived(self, data):
        print data
        self.sendData()
        
class CTimeClientFactory(protocol.ClientFactory):
    protocl = CTimerClient
    clientConnectionLost = clientConnectionFailed = lambda self, connector, reason: reactor.stop()

        
print "connect to {0}: {1}".format( HOST, PORT )
reactor.connectTCP(HOST, PORT, CTimeClientFactory())
reactor.run()