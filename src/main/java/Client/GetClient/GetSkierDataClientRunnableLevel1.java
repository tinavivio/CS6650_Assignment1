package Client.GetClient;

import Client.JerseyClient;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GetSkierDataClientRunnableLevel1 implements Runnable {
    
    private final int dayNumber;
    private final int beginIndex;
    private final int endIndex;
    private final JerseyClient client;
    private final ConcurrentMap<Long, Long[]> metrics;
    private final CountDownLatch countDownLatch;

    public GetSkierDataClientRunnableLevel1(int dayNumber, int beginIndex, int endIndex, JerseyClient client, ConcurrentMap<Long, Long[]> metrics, CountDownLatch countDownLatch) {
        this.dayNumber = dayNumber;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.client = client;
        this.metrics = metrics;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        
        // Instantiate a new Executor with a fixed size thread pool to process this thread's work
        // in a concurrent manner.
        Executor exec = Executors.newFixedThreadPool(3);
        // For each skier ID in the range that this thread is responsible for processing, 
        // submit a new task to the Executor with the skier ID.
        // Also pass each runnable task the ConcurrentHashMap, CountDownLatch, and JerseyClient.
        for (int i = this.beginIndex; i <= this.endIndex; i++){
            exec.execute(new GetSkierDataClientRunnableLevel2(i, dayNumber, this.client, this.metrics, this.countDownLatch));
        }       
            
    }
    
}
