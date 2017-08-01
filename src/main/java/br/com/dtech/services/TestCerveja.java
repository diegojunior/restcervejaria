package br.com.dtech.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/olaMundo")
public class TestCerveja {
	
	@GET
	@Produces("text/plain")
	public String olaMundo() {
		return "Ola Mundo";
	}
}
