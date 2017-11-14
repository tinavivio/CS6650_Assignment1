package Server.Service;

import Server.DAO.SkiDayStatsDAO;
import Server.Model.SkiDayStats;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/skiers")
public class SkiDayStatsService {
    
    @Autowired
    private SkiDayStatsDAO skiDayStatsDAO;
    
    @GET
    @Path("/myVert/{skierId},{dayNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkiDayStatsBySkierIdAndDay(@PathParam("skierId") int skierId, @PathParam("dayNumber") int dayNumber) {
        Long requestStartTime = System.currentTimeMillis();
        SkiDayStats skierStats = this.skiDayStatsDAO.getSkiDayStatsBySkierIdAndDay(skierId, dayNumber);
        Long dbResponseEndTime = System.currentTimeMillis();
        Long dbResponseTime = dbResponseEndTime - requestStartTime;
        if(skierStats != null){
            Long successfulResponseEndTime = System.currentTimeMillis();
            Long successfulResponseTime = successfulResponseEndTime - requestStartTime;
            return Response.status(200).entity(skierStats).build();
        }else{
            Long failedResponseEndTime = System.currentTimeMillis();
            Long failedResponseTime = failedResponseEndTime - requestStartTime;
            return Response.status(404).build();
        }
    }  
    
    @DELETE
    @Path("/delete/{dayNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllSKiDayStatsByDay(@PathParam("dayNumber") int dayNumber){
        int rowsAffected = this.skiDayStatsDAO.deleteAllSkiDayStatsByDay(dayNumber);
        return Response.ok(rowsAffected).build();
    }
    
    @DELETE
    @Path("/delete/{skierId},{dayNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteSkiDayStatsBySkierIdAndDay(@PathParam("skierId") int skierId, @PathParam("dayNumber") int dayNumber){
        int rowsAffected = this.skiDayStatsDAO.deleteSkiDayStatsBySkierIdAndDay(skierId, dayNumber);
        return Response.ok(rowsAffected).build();
    }
    
    @POST
    @Path("/load/{skierId},{dayNumber},{numRides},{totalVertical}")  
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postnewSkiDayStats(@PathParam("skierId") int skierId, @PathParam("dayNumber") int dayNumber, @PathParam("numRides") int numRides, @PathParam("totalVertical") int totalVertical) {
        Long requestStartTime = System.currentTimeMillis();
        SkiDayStats newStats = new SkiDayStats(skierId, dayNumber, numRides, totalVertical);
        Long dbRequestStartTime = System.currentTimeMillis();
        long newStatsId = this.skiDayStatsDAO.insertNewSkiDayStats(newStats);
        Long dbResponseEndTime = System.currentTimeMillis();
        Long dbResponseTime = dbResponseEndTime - dbRequestStartTime;
        if(newStatsId != -1){
            Long successfulResponseEndTime = System.currentTimeMillis();
            Long successfulResponseTime = successfulResponseEndTime - requestStartTime;
            return Response.status(201).entity(newStatsId).build();
        }else{
            Long failedResponseEndTime = System.currentTimeMillis();
            Long failedResponseTime = failedResponseEndTime - requestStartTime;
            return Response.status(409).build();
        }
    }
    
    @GET
    @Path("/{dayNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkiDayStatsByDay(@PathParam("dayNumber") int dayNumber) {
        List<Object[]> allStats = this.skiDayStatsDAO.getAllSkiDayStatsByDay(dayNumber);
        JSONArray jsonArr = new JSONArray();
        int index = 0;
        for (Object[] arr : allStats) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("skierId", arr[0]);
            jsonObject.put("numRides", arr[1]);
            jsonObject.put("totalVertical", arr[2]);
            jsonArr.put(index, jsonObject);
            index++;
        }
        return Response.ok(jsonArr.toString()).build();
    }
    
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNumSkiDayStats() {
        List<SkiDayStats> allStats = this.skiDayStatsDAO.getAllSkiDayStats();
        int num = allStats.size();
        return Response.ok(num).build();
    }
    
}
