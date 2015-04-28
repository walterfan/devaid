package com.github.walterfan.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * The Class ServiceMgr.
 * 
 * @author walter
 */
public class ServiceMgr extends AbstractService implements IServiceMgr {
    private static final int CAPACITY = 10;
    /** The logger. */
    private static Log logger = LogFactory.getLog(ServiceMgr.class);
    


    /** The name. */
    private String name = "ServiceMgr";


    /** The service map. */
    private List<IService> services = new ArrayList<IService>(CAPACITY);

    /**
     * Instantiates a new service mgr.
     */
    public ServiceMgr() {

    }

    /**
     * Gets the service map.
     * 
     * @return the service map
     */
    public int size() {
        return services.size();
    }

    /**
     * Sets the service map.
     * 
     * @param serviceList the service map
     */
    public void setServices(List<IService> serviceList) {
        this.services = serviceList;
    }

    /**
     * @param svc
     *            IService
     * @see com.webex.mdp.service.IServiceMgr#addService(com.webex.mdp.service.IService)
     */
    public void addService(IService svc) {
        if (svc == null) {
            return;
        }
       
        services.add(svc);
        
    }

    /**
     * @param svcName
     *            String
     * @return IService
     * @see com.webex.mdp.service.IServiceMgr#findService(java.lang.String)
     */
    public IService findService(String svcName) {
        for (IService svc : services) {
            if (svcName.equals(svc.getName())) {
                return svc;
            }
        }
        return null;
    }

    /**
     * @param svcName
     *            String
     * @see com.webex.mdp.service.IServiceMgr#removeService(java.lang.String)
     */
    public void removeService(String svcName) {
        services.remove(svcName);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.webex.mdp.service.AbstractService#onStart()
     */
    @Override
    protected void onStart() throws Exception {
        for (IService svc : services) {
            if (svc != null) {
                logger.info(svc.getName() + " start, isStarted="
                        + svc.isStarted());
                svc.start();

                logger.info(svc.getName() + " started, isStarted="
                        + svc.isStarted());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.webex.mdp.service.AbstractService#onStop()
     */
    @Override
    protected void onStop() throws Exception {
        ListIterator<IService> iter = this.services.listIterator(this.services.size());
        while (iter.hasPrevious()) {
         
            IService svc = iter.next();
            if (svc != null) {
                logger.info(svc.getName() + " stop, isStarted="
                        + svc.isStarted());
                try {
                    svc.stop();
                    logger.info(svc.getName() + " stopped, isStarted="
                            + svc.isStarted());
                } catch (Exception e) {
                    logger.error(svc + "stop error: ", e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.webex.mdp.service.AbstractService#onInit()
     */
    @Override
    protected void onInit() throws Exception {
        for (IService svc : services) {
            if (svc != null) {
                logger.info(svc.getName() + " init");
                svc.init();
                logger.info(svc.getName() + " inited");
            }
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.webex.mdp.service.AbstractService#onClean()
     */
    @Override
    protected void onClean() throws Exception {
        ListIterator<IService> iter = this.services.listIterator(this.services.size());
        while (iter.hasPrevious()) {
         
            IService svc = iter.previous();
            if (svc != null) {
                logger.info(svc.getName() + " clean");
                try {
                    svc.stop();
                    logger.info(svc.getName() + " cleaned");
                } catch (Exception e) {
                    logger.error(svc + "stop error: ", e);
                }
            }
        }
    }

    /**
     * @return Iterator<IService>
     * @see com.webex.mdp.service.IServiceMgr#iterator()
     */
    public Iterator<IService> iterator() {
        if (services == null) {
            return null;
        }
        return this.services.listIterator();
    }

    /**
     * @return String
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IService svc : services) {
            sb.append(svc.toString() + "\n");
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.webex.mdp.service.IService#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the status.
     * 
     * @param svcName
     *            the item
     * @return the status
     */
    public String getStatus(String svcName) {
        if (StringUtils.isNotBlank(svcName)) {
            IService svc = this.findService(svcName);
            if (svc != null) {
                return svc.toString();
            }
        }
        return toString();
    }

}
