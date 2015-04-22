package com.github.walterfan.server;

/**
 * @author walter
 * 
 */
public interface IServer {
    /**
     * Starts the process.
     * 
     * @throws Exception
     *             If the process fails to start
     */
    void start() throws Exception;

    /**
     * Stops the process. The process may wait for current activities to
     * complete normally, but it can be interrupted.
     * 
     * @exception Exception
     *                If the process fails to stop
     */
    void stop() throws Exception;

    boolean isStarted();
}
