package Server.Service;

import Server.DAO.LiftDAO;
import Server.Model.Lift;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/lifts")
public class LiftService {
    
    @Autowired
    private LiftDAO liftDAO;
            
    @POST
    @Path("/load/{liftNumber},{height}")  
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postNewLift(@PathParam("liftNumber") int liftNumber, @PathParam("height") int height) {
        Lift newLift = new Lift(liftNumber, height);
        long newLiftId = this.liftDAO.insertNewLift(newLift);
        if(newLiftId != -1){
            return Response.status(201).entity(newLiftId).build();
        }else{
            return Response.status(409).build();
        }   
    }
    
    @GET
    @Path("/height/{liftNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLiftHeightByLiftNumber(@PathParam("liftNumber") int liftNumber){
        Lift lift = this.liftDAO.getLiftByLiftNumber(liftNumber);
        if(lift != null){
            return Response.ok(lift.getHeight()).build();
        } else{
            return Response.status(404).build();
        }
    } 
    
    @DELETE
    @Path("/delete/{liftNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteLiftByLiftNumber(@PathParam("liftNumber") int liftNumber){
        int rowsAffected = this.liftDAO.deleteLiftByLiftNumber(liftNumber);
        return Response.ok(rowsAffected).build();
    }
    
    @PUT
    @Path("/update/{liftNumber},{height}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateLiftByLiftNumber(@PathParam("liftNumber") int liftNumber, @PathParam("height") int height){
        int rowsAffected = this.liftDAO.updateLiftByLiftNumber(liftNumber, height);
        return Response.ok(rowsAffected).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLifts() {
        List<Lift> allLifts = this.liftDAO.getAllLifts();
        GenericEntity entity = new GenericEntity<List<Lift>>(allLifts) {};
        return Response.ok(entity).build();
    }
    
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNumLifts() {
        List<Lift> allLifts = this.liftDAO.getAllLifts();
        int num = allLifts.size();
        return Response.ok(num).build();
    }
    
}
