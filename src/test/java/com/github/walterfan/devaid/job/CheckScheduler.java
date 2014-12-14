package com.github.walterfan.devaid.job;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/*
 * refer to 
 * http://quartz-scheduler.org/documentation/quartz-2.1.x/examples/Example3
 * http://stackoverflow.com/questions/17669445/java-example-dynamic-job-scheduling-with-quartz
 */
public class CheckScheduler {

	public static void main(String[] args) throws SchedulerException {
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		 
		JobKey jobKeyA = new JobKey("jobA", "group1");
    	JobDetail jobA = JobBuilder.newJob(CheckJob.class)
		.withIdentity(jobKeyA).build();
		
		Trigger trigger1 = TriggerBuilder
				.newTrigger()
				.withIdentity("dummyTriggerName1", "group1")
				.withSchedule(
					CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
				.build();
		
    	scheduler.start();
    	scheduler.scheduleJob(jobA, trigger1);
	} 
}


