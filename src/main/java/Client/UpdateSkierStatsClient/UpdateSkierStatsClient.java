package Client.UpdateSkierStatsClient;

import Client.JerseyClient;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class UpdateSkierStatsClient {
    
    private final int dayNumber;
    private final CountDownLatch countDownLatch;
    private final JerseyClient client;
    
    public UpdateSkierStatsClient(int dayNumber, CountDownLatch countDownLatch, JerseyClient client){
        this.dayNumber = dayNumber;
        this.countDownLatch = countDownLatch;
        this.client = client;
    }
    
    public void updateSkierStats() {
        
        // Instantiate a new Executor with a fixed size thread pool which will manage submission of runnable tasks.
        Executor exec = Executors.newFixedThreadPool(300);
        // Send a GET request to the server to obtain aggregate data for each skier based on the lift rides that are present in the database.
        // This will return a JSON array with one entry for each skier.
        // Each skier's entry is an array with the skier's ID, number of rides, and total vertical.
        Response response = this.client.getAllSkiDayStatsByDay(Response.class, Integer.toString(this.dayNumber));
        String responseAsString = response.readEntity(String.class);
        response.close();
        JSONArray responseAsJson = new JSONArray(responseAsString);
        // For each entry in the returned JSON array, submit a new task to the Executor with the details from the record.
        // Also pass each runnable task the CountDownLatch and JerseyClient.
        for (int i = 0; i < responseAsJson.length(); i++) {
            JSONObject obj = (JSONObject) responseAsJson.get(i);
            exec.execute(new UpdateSkierStatsClientRunnable((Integer) obj.get("skierId"), this.dayNumber, (Integer) obj.get("numRides"), (Integer) obj.get("totalVertical"), this.client, this.countDownLatch));
        }  

    }
    
}
