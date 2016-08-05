package com.github.walterfan.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * The Class ServiceMgr.
 * 
 * @author walter
 */
public class ServiceMgr extends AbstractService implements IServiceMgr {

    /** The logger. */
    private static Log logger = LogFactory.getLog(ServiceMgr.class);

    /** The name. */
    private String name = "ServiceMgr";

    /** The weblog. */
    //private WebLog weblog = WebLog.getInstance();

    /** The service map. */
    private Map<String, IService> serviceMap = new LinkedHashMap<String, IService>();
    
    private String enabledService = "";
    
    private Set<String> enabledServiceSet = new HashSet<String>();

    /**
     * Instantiates a new service mgr.
     */
    public ServiceMgr() {

    }

    
    
    public String getEnabledService() {
		return enabledService;
	}



	public void setEnabledService(String enabledService) {
		this.enabledService = enabledService;
		String[] arrService = enabledService.split(",");
		if(null == arrService || arrService.length == 0) {
			return;
		}
		
		for(String svcName: arrService) {
			this.enabledServiceSet.add(svcName.trim());
		}
	}

	public boolean isEnabledService(String serviceName) {
		boolean ret = false;
		if(enabledService.isEmpty()) {
			ret = true;
		}
		if(this.enabledServiceSet.contains(serviceName)) {
			ret = true;
		}
		logger.debug("isEnabledService: " + serviceName + ", ret=" + ret);
		return ret;
	}

	/**
     * Gets the service map.
     * 
     * @return the service map
     */
    public int size() {
        return serviceMap.size();
    }

    /**
     * Sets the service map.
     * 
     * @param serviceMap
     *            the service map
     */
    public void setServiceMap(Map<String, IService> serviceMap) {
        this.serviceMap = serviceMap;
    }

    /**
     * @param svc
     *            IService
     * @see com.github.walterfan.service.IServiceMgr#addService(com.github.walterfan.service.IService)
     */
    public void addService(IService svc) {
        if (svc == null) {
            return;
        }
        if (serviceMap.get(svc.getName()) == null) {
            serviceMap.put(svc.getName(), svc);
        }
    }

    /**
     * @param svcName
     *            String
     * @return IService
     * @see com.github.walterfan.service.IServiceMgr#findService(java.lang.String)
     */
    public IService findService(String svcName) {
        return serviceMap.get(svcName);
    }

    /**
     * @param svcName
     *            String
     * @see com.github.walterfan.service.IServiceMgr#removeService(java.lang.String)
     */
    public void removeService(String svcName) {
        serviceMap.remove(svcName);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.walterfan.service.AbstractService#onStart()
     */
    @Override
    protected void onStart() throws Exception {
        for (Map.Entry<String, IService> entry : serviceMap.entrySet()) {
        	String svcName = entry.getKey();
        	if(!this.isEnabledService(svcName)) {
        		continue;
        	}
        	
            IService svc = entry.getValue();
            if (svc != null) {
                logger.info(svc.getName() + " start..., isStarted="
                        + svc.isStarted());
                svc.start();
                logger.info(svc.getName() + " started, isStarted="
                        + svc.isStarted());
                //weblog.info(svc.getName() + " started, isStarted= "
                //        + svc.isStarted());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.walterfan.service.AbstractService#onStop()
     */
    @Override
    protected void onStop() throws Exception {
        for (Map.Entry<String, IService> entry : serviceMap.entrySet()) {
        	String svcName = entry.getKey();
        	if(!this.isEnabledService(svcName)) {
        		continue;
        	}
            IService svc = entry.getValue();
            if (svc != null) {
                logger.info(svc.getName() + " stop..., isStarted="
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
     * @see com.github.walterfan.service.AbstractService#onInit()
     */
    @Override
    protected void onInit() throws Exception {
        for (Map.Entry<String, IService> entry : serviceMap.entrySet()) {
        	String svcName = entry.getKey();
        	if(!this.isEnabledService(svcName)) {
        		continue;
        	}
            IService svc = entry.getValue();
            if (svc != null) {
                logger.info(svc.getName() + " init..., isStarted="
                        + svc.isStarted());
                svc.init();
                logger.info(svc.getName() + " inited, isStarted="
                        + svc.isStarted());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.walterfan.service.AbstractService#onClean()
     */
    @Override
    protected void onClean() throws Exception {
        for (Map.Entry<String, IService> entry : serviceMap.entrySet()) {
        	String svcName = entry.getKey();
        	if(!this.isEnabledService(svcName)) {
        		continue;
        	}
            IService svc = entry.getValue();
            if (svc != null) {
                logger.info(svc.getName() + " clean..., isStarted="
                        + svc.isStarted());
                svc.clean();
                logger.info(svc.getName() + " cleaned, isStarted="
                        + svc.isStarted());
            }
        }
    }

    /**
     * @return Iterator<IService>
     * @see com.github.walterfan.service.IServiceMgr#iterator()
     */
    public Iterator<IService> iterator() {
        if (serviceMap == null) {
            return null;
        }
        return this.serviceMap.values().iterator();
    }

    /**
     * @return String
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, IService> entry : serviceMap.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.walterfan.service.IService#getName()
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
     * @param item
     *            the item
     * @return the status
     */
    public String getStatus(String item) {
        if (StringUtils.isNotBlank(item)) {
            IService svc = serviceMap.get(item);
            if (svc != null) {
                return svc.toString();
            }
        }
        return toString();
    }

}
