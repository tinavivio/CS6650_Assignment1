package Server.Service;

import Server.DAO.LiftRideDAO;
import Server.Model.LiftRide;
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

@Path("/liftRides")
public class LiftRideService {
    
    @Autowired
    private LiftRideDAO liftRideDAO;
    
    @POST
    @Path("/load/{resortId},{dayNumber},{time},{skierId},{liftNumber}")  
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postnewLiftRide(@PathParam("resortId") int resortId, @PathParam("dayNumber") int dayNumber, @PathParam("time") int time, @PathParam("skierId") int skierId, @PathParam("liftNumber") int liftNumber) {
        LiftRide newLiftRide = new LiftRide(resortId, dayNumber, skierId, liftNumber, time);
        long newLiftRideId = this.liftRideDAO.insertNewLiftRide(newLiftRide);
        return Response.status(201).entity(newLiftRideId).build();
    }  
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLiftRides() {
        List<LiftRide> allLiftRides = this.liftRideDAO.getAllLiftRides();
        GenericEntity entity = new GenericEntity<List<LiftRide>>(allLiftRides) {};
        return Response.ok(entity).build();
    }
    
    @GET
    @Path("/{skierId},{dayNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLiftRidesBySkierIdAndDay(@PathParam("skierId") int skierId, @PathParam("dayNumber") int dayNumber) {
        List<LiftRide> allLiftRides = this.liftRideDAO.getAllLiftRidesBySkierIdAndDay(skierId, dayNumber);
        GenericEntity entity = new GenericEntity<List<LiftRide>>(allLiftRides) {};
        return Response.ok(entity).build();
    }
    
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNumLiftRides() {
        List<LiftRide> allLiftRides = this.liftRideDAO.getAllLiftRides();
        int num = allLiftRides.size();
        return Response.ok(num).build();
    }
    
    @DELETE
    @Path("/delete/{dayNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllLiftRidesByDay(@PathParam("dayNumber") int dayNumber){
        int rowsAffected = this.liftRideDAO.deleteAllLiftRidesByDay(dayNumber);
        return Response.ok(rowsAffected).build();
    }   

}
