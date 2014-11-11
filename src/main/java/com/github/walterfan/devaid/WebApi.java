package com.github.walterfan.devaid;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class WebApi {
	@GET
	@Produces("text/html")
	public Response index() throws URISyntaxException {
		//InputStream in = this.getClass().getClassLoader().getResourceAsStream("index.html");
		File f = new File(System.getProperty("user.dir") + "/index.html");
		String mt = new MimetypesFileTypeMap().getContentType(f);
		return Response.ok(f, mt).build();
	}

	@GET
	@Path("/hello")
	public Response helloGet() {
		return Response.status(200).entity("HTTP GET method called").build();
	}

	@POST
	@Path("/hello")
	public Response helloPost() {
		return Response.status(200).entity("HTTP POST method called").build();
	}
}