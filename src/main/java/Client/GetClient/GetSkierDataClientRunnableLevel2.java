package Client.GetClient;

import Client.JerseyClient;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import javax.ws.rs.core.Response;

public class GetSkierDataClientRunnableLevel2 implements Runnable {
    
    private final int skierId;
    private final int dayNumber;
    private final JerseyClient client;
    private final ConcurrentMap<Long, Long[]> metrics;
    private final CountDownLatch countDownLatch;

    public GetSkierDataClientRunnableLevel2(int skierId, int dayNumber, JerseyClient client, ConcurrentMap<Long, Long[]> metrics, CountDownLatch countDownLatch) {
        this.skierId = skierId;
        this.dayNumber = dayNumber;
        this.client = client;
        this.metrics = metrics;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        
        System.out.println("Thread " + Thread.currentThread().toString() + " getting day " + Integer.toString(this.dayNumber) + " ski stats for skier " + Integer.toString(this.skierId));
        Long getStartTime = System.currentTimeMillis();
        try{
            Response response = this.client.getSkiDayStatsBySkierIdAndDay(Response.class, Integer.toString(this.skierId), Integer.toString(this.dayNumber));
            Long getEndTime = System.currentTimeMillis();
            response.close();
            Long getResponseTime = getEndTime - getStartTime;
            Long responseSuccessful;
            if (response.getStatus() == 200){
                responseSuccessful = new Long(1);
            }else{
                System.err.println("Unsuccessful response, status: " + response.getStatus());
                responseSuccessful = new Long(0);
            }
            Long[] arr = {getStartTime, getResponseTime, responseSuccessful};
            this.metrics.put(new Long(this.skierId), arr);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Long[] arr = {getStartTime, new Long(-1), new Long(0)};
            this.metrics.put(new Long(this.skierId), arr);
        }
        
        this.countDownLatch.countDown();
        System.out.println("CountDownLatch count: " + this.countDownLatch.getCount());
            
    }
    
}
