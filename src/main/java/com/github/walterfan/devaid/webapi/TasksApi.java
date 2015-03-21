/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.walterfan.devaid.webapi;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
*
* 
 * @author walter
 */
@Path("/api/v1/taks")
public class TasksApi {
    
    //POST /tasks：create a task
    public Response create() {
        return Response.ok().build();
    }
    
    //GET /tasks/ID：retrieve a task
    public Response read() {
        return Response.ok().build();
    }
    
    //PUT /tasks/ID： update a task with all fields
    //PATCH /tasks/ID：update a task with part of fields
    public Response update() {
        return Response.ok().build();
    }
    
    //DELETE /tasks/ID：delete a task
    public Response delete() {
        return Response.ok().build();
    }
    
    //GET /tasks：list all task
    public Response list() {
        return Response.ok().build();
    }
}
