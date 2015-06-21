package com.github.walterfan.service;

import com.github.walterfan.server.AbstractServer;



/**
 * The Class AbstractService.
 *
 * @author walter
 */
public abstract class AbstractService extends AbstractServer implements IService {

    /**
     * On init.
     *
     * @throws Exception the exception
     */
    protected abstract void onInit() throws Exception;

    /**
     * On clean.
     *
     * @throws Exception the exception
     */
    protected abstract void onClean() throws Exception;

 
    /**
     * 
     * @exception Exception error
     */
    public void init() throws Exception {
        assert (!this.isStarted());
        onInit();
    }

    /**
     * 
     * @exception Exception error
     */
    public void clean() throws Exception {
        assert (!this.isStarted());
        onClean();
    }

    
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
