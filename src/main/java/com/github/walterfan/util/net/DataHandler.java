package com.github.walterfan.util.net;


public interface DataHandler {

    public void onConnect(String serverIp,  int serverPort);

    public void onDisconnect();

    public int slice(byte[] bytes, int byteCount);
    
    public void onReceive(byte[] bytes, int byteCount);
    
    public void onSend(byte[] bytes, int byteCount);
}
