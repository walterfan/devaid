package com.github.walterfan.server;


/**
 * The Class AbstractService.
 *
 * @author walter
 */
public abstract class AbstractServer implements IServer {

    /** The is started_. */
    private volatile boolean isStarted = false;

    /**
     * On start.
     *
     * @throws Exception the exception
     */
    protected abstract void onStart() throws Exception;

    /**
     * On stop.
     *
     * @throws Exception the exception
     */
    protected abstract void onStop() throws Exception;

        /**
     * 
     * @exception Exception error
     */
    public void start() throws Exception {
        if (this.isStarted) {
            return;
        }
        this.onStart();
        this.isStarted = true;
    }

    /**
     * 
     * @exception Exception error
     */
    public void stop() throws Exception {
        if (!this.isStarted) {
            return;
        }
        this.onStop();
        this.isStarted = false;
    }

     /**
     * @return started or not
     *
     */
    public boolean isStarted() {
        return this.isStarted;
    }

}
