package com.github.muirandy.docs.living.log.restful;

import com.github.muirandy.docs.living.api.Log;
import com.github.muirandy.docs.living.api.Logs;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Consumes("application/json")
@Produces("application/json")
public interface LoggerClient {
    @POST
    @Path("/log")
    Response log(Log log);

    @GET
    @Path("/log/read")
    Logs readAll();

    @GET
    @Path("/log/read/{marker}")
    Logs read(@PathParam("marker") String marker);

    @POST
    @Path("/log/markEnd/{marker}")
    Response markEnd(@PathParam("marker") String marker);
}