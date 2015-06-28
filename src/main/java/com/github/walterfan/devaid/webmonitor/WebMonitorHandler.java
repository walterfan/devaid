package com.github.walterfan.devaid.webmonitor;

/**
 * @author walter
 *
 */
public interface WebMonitorHandler extends WebHandler {
    void setWebMonitor(WebMonitor monitor);
    void setWebRender(WebRender render);
}
