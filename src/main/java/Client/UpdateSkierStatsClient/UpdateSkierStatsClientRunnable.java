package Client.UpdateSkierStatsClient;

import Client.JerseyClient;
import java.util.concurrent.CountDownLatch;
import javax.ws.rs.core.Response;

public class UpdateSkierStatsClientRunnable implements Runnable {

    private final int skierId;
    private final int dayNumber;
    private final int numRides;
    private final int totalVertical;
    private final JerseyClient client;
    private final CountDownLatch countDownLatch;
    
    public UpdateSkierStatsClientRunnable(int skierId, int dayNumber, int numRides, int totalVertical, JerseyClient client, CountDownLatch countDownLatch) {
        this.skierId = skierId;
        this.dayNumber = dayNumber;
        this.numRides = numRides;
        this.totalVertical = totalVertical;
        this.client = client;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("Thread " + Thread.currentThread().toString() + " updating day " + Integer.toString(this.dayNumber) + " stats for skier : " + Integer.toString(this.skierId));
        try{
            // Send a POST request to the server with a skier's daily statistics.
            Response response = this.client.postNewSkiDayStats(Integer.toString(this.skierId), Integer.toString(this.dayNumber), Integer.toString(this.numRides), Integer.toString(this.totalVertical));
            response.close();
            if (response.getStatus() == 201){
                System.out.println("Successful response!");
            }else{
                System.err.println("Unsuccessful response, status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        // Count down the CountDownLatch.
        this.countDownLatch.countDown();
        System.out.println("CountDownLatch count: " + this.countDownLatch.getCount());
    }
    
}
