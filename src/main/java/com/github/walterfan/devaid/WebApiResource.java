package com.github.walterfan.devaid;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.github.walterfan.util.FileUtil;

@Path("/api")
public class WebApiResource {
	private static final Logger logger = Log.getLogger(WebApiResource.class);
	
	private String staticPath;
	
	public WebApiResource() {
		staticPath = "./site/static";
	}
	
	@GET
	@Produces("text/html")
	public Response index() throws URISyntaxException {
		
		//System.setProperty("user.dir", "./site");
		File f = new File(staticPath + "/index.html");
		String mt = new MimetypesFileTypeMap().getContentType(f);
		return Response.ok(f, mt).build();
		//return Response.status(200).entity("task api").build();
	}

	
	@GET
	@POST
	@Path("static/{path}")
	@Produces("text/plain")
	public Response staticGet(@PathParam("path") String path) {
		File file = new File(staticPath + "/" + path);
		try {
			byte[] bytes = FileUtil.readFromFile(file);
			return Response.status(200).entity(bytes).build();
		} catch (IOException e) {		
			return Response.status(404).entity("not found the file").build();
		}
		
	}
	//read
	@GET
	@Path("task")
	@Produces("text/plain")
	public Response helloGet() {
		return Response.status(200).entity("HTTP GET method called").build();
	}
	//put
	@POST
	@Path("task")
	@Produces("text/plain")
	public Response helloPost() {
		return Response.status(200).entity("HTTP POST method called").build();
	}
	
	@PUT
	@Path("task")
	@Produces("text/plain")
	public Response helloPut() {
		return Response.status(200).entity("HTTP PUT method called").build();
	}
	
	@DELETE
	@Path("task")
	@Produces("text/plain")
	public Response helloDelete() {
		return Response.status(200).entity("HTTP DELETE method called").build();
	}
}