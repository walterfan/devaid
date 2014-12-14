package com.github.walterfan.devaid;

import java.io.File;
import java.net.URISyntaxException;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/api")
public class WebApiResource {
	@GET
	@Produces("text/html")
	public Response index() throws URISyntaxException {
		//InputStream in = this.getClass().getClassLoader().getResourceAsStream("index.html");
		System.setProperty("user.dir", ".");
		File f = new File(System.getProperty("user.dir") + "/index.html");
		String mt = new MimetypesFileTypeMap().getContentType(f);
		return Response.ok(f, mt).build();
		//return Response.status(200).entity("task api").build();
	}

	@GET
	@Path("hello")
	@Produces("text/plain")
	public Response helloGet() {
		return Response.status(200).entity("HTTP GET method called").build();
	}

	@POST
	@Path("hello")
	@Produces("text/plain")
	public Response helloPost() {
		return Response.status(200).entity("HTTP POST method called").build();
	}
}