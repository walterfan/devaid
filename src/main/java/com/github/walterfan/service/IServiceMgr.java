package com.github.walterfan.service;

import java.util.Iterator;



/**
 * @author walter
 *
 */
public interface IServiceMgr extends IService, Iterable<IService> {
    void addService(IService svc);
    
    void removeService(String svcName);
    
    IService findService(String svcName);
    
    Iterator<IService> iterator ();

}
