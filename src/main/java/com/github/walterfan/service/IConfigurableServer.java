package com.github.walterfan.service;

/**
 * The Interface IConfigurableServer.
 *
 * @author walter
 */
public interface IConfigurableServer extends IServer {

    /**
     * Resume.
     *
     * @throws Exception the exception
     */
    void resume() throws Exception;

    /**
     * Reconfig.
     *
     * @throws Exception the exception
     */
    void reconfig() throws Exception;

}
