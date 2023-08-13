package com.github.muirandy.docs.living.log.restful;

import com.github.muirandy.docs.living.api.Log;
import com.github.muirandy.docs.living.api.Logs;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

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

    @GET
    @Path("/log/read/gm/{marker}")
    List<Logs> readGoldenMaster(@PathParam("marker") String sequence_diagram_id);

    @POST
    @Path("/log/markEnd/{marker}")
    Response markEnd(@PathParam("marker") String marker);

    @POST
    @Path("/log/markGm/{marker}")
    Response markGoldenMaster(@PathParam("marker") String sequence_diagram_id);
}
