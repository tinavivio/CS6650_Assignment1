package Client.PostClient;

import Client.JerseyClient;
import Server.Model.LiftRide;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import javax.ws.rs.core.Response;

public class PostSkierDataClientRunnable implements Runnable {
    
    private final String resortId;
    private final String dayNumber;
    private final String time;
    private final String skierId;
    private final String liftNumber;
    private final JerseyClient client;
    private final ConcurrentMap<LiftRide, Long[]> metrics;
    private final CountDownLatch countDownLatch;
 
    public PostSkierDataClientRunnable(String resortId, String dayNumber, String time, String skierId, String liftNumber, JerseyClient client, ConcurrentMap<LiftRide, Long[]> metrics, CountDownLatch countDownLatch) {
        this.resortId = resortId;
        this.dayNumber = dayNumber;
        this.time = time;
        this.skierId = skierId;
        this.liftNumber = liftNumber;
        this.client = client;
        this.metrics = metrics;
        this.countDownLatch = countDownLatch;
    }
 
    @Override
    public void run() {
        
        System.out.println("Thread " + Thread.currentThread().toString() + " posting the following data: " + this.toString());
        Long postStartTime = System.currentTimeMillis();
        try{
            Response response = this.client.postNewLiftRide(this.resortId, this.dayNumber, this.time, this.skierId, this.liftNumber);
            Long postEndTime = System.currentTimeMillis();
            response.close();
            Long postResponseTime = postEndTime - postStartTime;
            Long responseSuccessful;
            if (response.getStatus() == 201){
                responseSuccessful = new Long(1);
            }else{
                System.err.println("Unsuccessful response, status: " + response.getStatus());
                responseSuccessful = new Long(0);
            }
            Long[] arr = {postStartTime, postResponseTime, responseSuccessful};
            this.metrics.put(new LiftRide(Integer.parseInt(this.resortId), Integer.parseInt(this.dayNumber), Integer.parseInt(this.skierId), Integer.parseInt(this.liftNumber), Integer.parseInt(this.time)), arr);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Long[] arr = {postStartTime, new Long(-1), new Long(0)};
            this.metrics.put(new LiftRide(Integer.parseInt(this.resortId), Integer.parseInt(this.dayNumber), Integer.parseInt(this.skierId), Integer.parseInt(this.liftNumber), Integer.parseInt(this.time)), arr);          
        }
        
        this.countDownLatch.countDown();
        System.out.println("CountDownLatch count: " + this.countDownLatch.getCount());
        
    }

    @Override
    public String toString() {
        return "Data{" + "resortId=" + this.resortId + ", dayNumber=" + this.dayNumber + ", time=" + this.time + ", skierId=" + this.skierId + ", liftNumber=" + this.liftNumber + '}';
    }
    
}
