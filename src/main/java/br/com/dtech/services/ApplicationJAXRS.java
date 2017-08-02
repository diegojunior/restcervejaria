package br.com.dtech.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.jettison.JettisonFeature;

public class ApplicationJAXRS extends Application {

	@Override
	public Map<String, Object> getProperties() {
		final Map<String, Object> properties = new HashMap<>();
		properties.put("jersey.config.server.provider.packages", "br.com.dtech.services");
		
		return properties;
	}
	
	@Override
	public Set<Object> getSingletons() {
		
		final Set<Object> singletons = new HashSet<Object>();
		singletons.add(new JettisonFeature());
		
		return singletons;
	}
}
