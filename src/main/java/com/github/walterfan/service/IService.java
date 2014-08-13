package com.github.walterfan.service;

/**
 * @author walter
 * 
 */
public interface IService extends IServer {

    /**
     * Initiate the process. The process may wait for current activities to
     * complete normally, but it can be interrupted.
     * 
     * @exception Exception
     *                If the process fails to stop
     */
    void init() throws Exception;

    /**
     * Cleanup the process. The process may wait for current activities to
     * complete normally, but it can be interrupted.
     * 
     * @exception Exception
     *                If the process fails to stop
     */
    void clean() throws Exception;
    
    /**
     * @return service name
     */
    String getName();

}
