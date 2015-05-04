package com.github.walterfan.server;

import com.github.walterfan.service.AbstractService;
import com.github.walterfan.service.GlobalBeanFactory;
import com.github.walterfan.service.IService;

public abstract class ServiceWrapper extends AbstractService {
	
	private static final String serviceName = "serviceMgr";
	
	private IService appService;
	
	private GlobalBeanFactory beanFactory;
	
	public abstract String[] getContextFiles() ;
	
	@Override
	protected void onInit() throws Exception {
		GlobalBeanFactory.setXmlConfigFiles(this.getContextFiles());
		beanFactory = GlobalBeanFactory.getInstance();
		appService = (IService)beanFactory.getBean(serviceName);
		appService.init();
	}

	@Override
	protected void onStop() throws Exception {
		appService.stop();

	}
	
	@Override
	protected void onStart() throws Exception {
		appService.start();

	}

	@Override
	protected void onClean() throws Exception {
		appService.clean();

	}

}
