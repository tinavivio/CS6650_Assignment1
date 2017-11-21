package Server.Service;

import Server.DAO.MetricsDAO;
import Server.Model.Metrics;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/metrics")
public class MetricsService {
    
    @Autowired
    private MetricsDAO metricsDAO;
     
    @POST
    @Path("/load/{serverId},{databaseRequestTime},{databaseResponseTime},{serverRequestTime},{serverResponseTime},{responseCode}")  
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postnewMetrics(@PathParam("serverId") String serverId, @PathParam("databaseRequestTime") Long databaseRequestTime, @PathParam("databaseResponseTime") Long databaseResponseTime, @PathParam("serverRequestTime") Long serverRequestTime, @PathParam("serverResponseTime") Long serverResponseTime, @PathParam("responseCode") Long responseCode) {
        Metrics newMetrics = new Metrics(serverId, databaseRequestTime, databaseResponseTime, serverRequestTime, serverResponseTime, responseCode);
        long newMetricsId = this.metricsDAO.insertNewMetrics(newMetrics);
        return Response.status(201).entity(newMetricsId).build();
    }  
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMetrics() {
        List<Metrics> allMetrics = this.metricsDAO.getAllMetrics();
        GenericEntity entity = new GenericEntity<List<Metrics>>(allMetrics) {};
        return Response.ok(entity).build();
    }
    
    @GET
    @Path("/{serverId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetricsByServerId(@PathParam("serverId") String serverId) {
        List<Metrics> metrics = this.metricsDAO.getMetricsByServerId(serverId);
        GenericEntity entity = new GenericEntity<List<Metrics>>(metrics) {};
        return Response.ok(entity).build();
    }
    
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNumMetrics() {
        List<Metrics> allMetrics = this.metricsDAO.getAllMetrics();
        int num = allMetrics.size();
        return Response.ok(num).build();
    }
    
    @GET
    @Path("/minDatabaseRequestTime")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMinDatabaseRequestTime(){
        Long minDatabaseRequestTime = this.metricsDAO.getMinDatabaseRequestTime();
        return Response.ok(minDatabaseRequestTime).build();
    }
    
    @GET
    @Path("/minServerRequestTime")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMinServerRequestTime(){
        Long minServerRequestTime = this.metricsDAO.getMinServerRequestTime();
        return Response.ok(minServerRequestTime).build();
    }
    
    @GET
    @Path("/minDatabaseRequestTime/{serverId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMinDatabaseRequestTimeByServerId(@PathParam("serverId") String serverId){
        Long minDatabaseRequestTime = this.metricsDAO.getMinDatabaseRequestTimeByServerId(serverId);
        return Response.ok(minDatabaseRequestTime).build();
    }
    
    @GET
    @Path("/minServerRequestTime/{serverId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMinServerRequestTimeByServerId(@PathParam("serverId") String serverId){
        Long minServerRequestTime = this.metricsDAO.getMinServerRequestTimeByServerId(serverId);
        return Response.ok(minServerRequestTime).build();
    }
    
    @DELETE
    @Path("/delete")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllMetrics(){
        int rowsAffected = this.metricsDAO.deleteAllMetrics();
        return Response.ok(rowsAffected).build();
    }   
    
}
