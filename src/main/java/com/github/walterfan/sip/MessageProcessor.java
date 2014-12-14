package com.github.walterfan.sip;
//refer to http://www.oracle.com/technetwork/java/introduction-jain-sip-090386.html
public interface MessageProcessor
{
    public void processMessage(String sender, String message);
    public void processError(String errorMessage);
    public void processInfo(String infoMessage);
}
