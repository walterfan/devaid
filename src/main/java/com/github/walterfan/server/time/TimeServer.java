package com.github.walterfan.server.time;

import com.github.walterfan.server.AbstractServer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by walter on 6/18/16.
 */
public class TimeServer extends AbstractServer {

    public static final int BACKLOG = 1024;
    public static final int CAPACITY = 1024;

    public static final String CHARSET_NAME = "UTF-8";
    private Log logger = LogFactory.getLog(TimeServer.class);

    private Selector mySelector;

    private ServerSocketChannel myChannel;

    private volatile boolean myStopFlag;

    private ExecutorService executor ;

    public TimeServer(int port) {
        try {
            mySelector = Selector.open();
            myChannel = ServerSocketChannel.open();
            myChannel.configureBlocking(false);
            myChannel.socket().bind(new InetSocketAddress(port), BACKLOG);
            myChannel.register(mySelector, SelectionKey.OP_ACCEPT);


        } catch (IOException e) {
            logger.error("TimeServer create failed ", e);
        }

    }



    public void run() {
        while(myStopFlag) {
            try {
                mySelector.select(1000);
                Set<SelectionKey> selectionKeys = mySelector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();

                SelectionKey key = null;
                while(it.hasNext()) {
                    key = it.next();
                    it.remove();

                    try {
                        handleData(key);
                    } catch (IOException ex) {
                        if(key != null) {
                            key.cancel();
                            if(null != key.channel()) key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        if(null != mySelector) {
            try {
				mySelector.close();
			} catch (IOException e) {
				
			}
        }
    }

    public void handleData(SelectionKey key) throws IOException {
        if(!key.isValid())
            return;

        if(key.isAcceptable()) {
            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(mySelector, SelectionKey.OP_READ);
        }

        if(key.isReadable()) {
            SocketChannel sc = (SocketChannel)key.channel();

            ByteBuffer readBuffer = ByteBuffer.allocate(CAPACITY);
            int readBytes = sc.read(readBuffer);

            if(readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);

                String queryStr = new String(bytes, CHARSET_NAME);
                System.out.println(queryStr);

                String timeStr = new Date().toString();
                byte[] timeBytes = timeStr.getBytes();
                ByteBuffer writeBuffer = ByteBuffer.allocate(timeBytes.length);
                writeBuffer.put(timeBytes);
                writeBuffer.flip();
                sc.write(writeBuffer);

            } else {
                key.cancel();
                if(null != sc) sc.close();
            }
        }
    }

    @Override
    protected void onStart() throws Exception {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onStop() throws Exception {
        myStopFlag = true;
        executor.shutdownNow();
    }

    public static void main(String[] args) throws Exception {
        TimeServer svr = new TimeServer(2016);
        svr.start();
        TimeUnit.SECONDS.sleep(10);
        svr.stop();

    }
 }
