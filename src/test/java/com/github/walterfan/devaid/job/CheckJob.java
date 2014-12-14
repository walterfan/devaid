package com.github.walterfan.devaid.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CheckJob  implements Job { 
	public void execute(JobExecutionContext arg0) throws JobExecutionException{ 
		System.out.println("Check on " + new Date()); 
		//call testRegisterAndUnRegisterFromTAS200_toHeader_sip
		} 
	} 