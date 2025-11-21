package org.example.web.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
public class HealthResource implements JaxRSResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String healthCheck() {
        return "{\"isAvailable\":true}";
    }
}
