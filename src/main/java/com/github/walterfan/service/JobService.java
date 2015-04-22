package com.github.walterfan.service;


import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author walter
 *
 */
public class JobService implements IService{

    private Scheduler jobScheduler;
  
    
    public JobService() {
    	
    }
    
    public void setJobScheduler(Scheduler sched) {
    	this.jobScheduler = sched;
    }
    
    public void init() throws Exception {
    	
    }

    public void start() throws Exception {    	
    	jobScheduler.start();
	}
    
    @Override
	public String toString() {
		return jobScheduler.toString();
	}

	public void stop()throws Exception{
    	
    	jobScheduler.standby();
    }


	public void clean() throws Exception {
		jobScheduler.shutdown();
		
	}

	public String getName() {
		return "jobService";
	}

	public boolean isStarted() {
		try {
			return !jobScheduler.isShutdown();
		} catch (SchedulerException e) {
			return false;
		}

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		  System.out.println("Job service start.");  
		  ApplicationContext context = new ClassPathXmlApplicationContext("mdp-job.xml");  
		  
		  //Scheduler sched = (Scheduler)context.getBean("jobScheduler");
		  JobService jobService = (JobService)context.getBean("jobService");
		  //jobService.setJobScheduler(sched);
		  
		  jobService.init();
		  jobService.start();
		  
		  Thread.sleep(3600000);
		  
		  jobService.stop();
		  jobService.clean();
		  
		  System.out.print("Job Service end..");        
	}

}
