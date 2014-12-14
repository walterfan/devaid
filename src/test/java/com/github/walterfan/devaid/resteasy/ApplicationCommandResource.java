package com.github.walterfan.devaid.resteasy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("commands")
public class ApplicationCommandResource {

	@GET
	@Path("all")
	@Produces(MediaType.TEXT_PLAIN)
	public String[] getCommands() {
		return (new String[] { "start", "stop", "loglevel" });

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stop/{process_name}/{command_arg0}/{command_arg1}")
	public CommandResponse doStopCommand(
			@PathParam("process_name") String processName,
			@PathParam("command_arg0") String arg0,
			@PathParam("command_arg1") String arg1) {

		return (new CommandResponse(CommandResponse.OK, String.format(
				"Stop Command executed ok on [%s].", processName)));

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("start/{process_name}/{command_arg0}/{command_arg1}")
	public CommandResponse doStartCommand(
			@PathParam("process_name") String processName,
			@PathParam("command_arg0") String arg0,
			@PathParam("command_arg1") String arg1) {

		return (new CommandResponse(CommandResponse.OK, String.format(
				"Start Command executed ok on [%s].", processName)));

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("loglevel/{process_name}/{command_arg0}")
	public CommandResponse doChangeLogLevel(
			@PathParam("process_name") String processName,
			@PathParam("command_arg0") String arg0)
			 {

		return (new CommandResponse(CommandResponse.OK, String.format(
				"doChangeLogLevel Command executed ok on [%s].", processName)));

	}		

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello() {
		return (Response.ok("Test OK!").build());
	}
	
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public Response  helloGet() {     
	  return Response.status(200).entity("HTTP GET method called").build();
	}
}
