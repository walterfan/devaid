/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.walterfan.devaid.webapi;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**

GET /links：list all link
POST /links：create a link
GET /links/ID：retrieve a link
PUT /links/ID： update a link with all fields
PATCH /links/ID：update a link with part of fields
DELETE /links/ID：delete a link
GET /links/ID/tags：list all tags of a link
DELETE /links/ID/tags/ID：delete a tag of a link
* 
 * @author walter
 */
@Path("/api/v1/links")
public class LinksApi {
    
    //POST /links：create a link
    public Response create() {
        return Response.ok().build();
    }
    
    //GET /links/ID：retrieve a link
    public Response read() {
        return Response.ok().build();
    }
    
    //PUT /links/ID： update a link with all fields
    //PATCH /links/ID：update a link with part of fields
    public Response update() {
        return Response.ok().build();
    }
    
    //DELETE /links/ID：delete a link
    public Response delete() {
        return Response.ok().build();
    }
    
    //GET /links：list all link
    public Response list() {
        return Response.ok().build();
    }
    
    //GET /links/search：search a link
    public Response search() {
        return Response.ok().build();
    }
}
