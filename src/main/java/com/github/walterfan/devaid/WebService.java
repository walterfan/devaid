package com.github.walterfan.devaid;


import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;


public class WebService extends Application  {
 private static Set services = new HashSet(); 
 public  WebService() {     
   // initialize restful services   
   services.add(new WebApi());  
 }
 @Override
 public  Set getSingletons() {
  return services;
 }  
 public  static Set getServices() {  
  return services;
 } 
}