from twisted.internet import protocol, reactor
from time import ctime

PORT = 21567

class CTimerServer(protocol.Protocol):
    def connectionMade(self):
        clnt = self.ckbt = self.transport.getPeer().host
        print "connect from ", clnt
        
    def dataReceived(self, data):
        self.transport.write('[%s] %s' % (ctime(), data))
        
factory = protocol.Factory()
factory.protocol = CTimerServer
print "waiting for connection..."
reactor.listenTCP(PORT, factory)
reactor.run()