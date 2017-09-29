package Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class IterativeHttpRequester implements Runnable {

    private final JerseyClient client;
    private final CountDownLatch latch;
    private final int numIterations;
    private int numRequestsSent;
    private int numResponsesSuccessful;
    private final List<Long[]> requestAndResponseTimes;

    public IterativeHttpRequester(JerseyClient client, CountDownLatch latch, int numIterations) {
        this.client = client;
        this.latch = latch;
        this.numIterations = numIterations;
        this.numRequestsSent = 0;
        this.numResponsesSuccessful = 0;
        this.requestAndResponseTimes = new ArrayList<Long[]>();
    }

    public synchronized int getNumRequestsSent() {
        return this.numRequestsSent;
    }

    public synchronized int getNumResponsesSuccessful() {
        return this.numResponsesSuccessful;
    }
    
    public synchronized List<Long[]> getRequestAndResponseTimes() {
        List<Long[]> newList = new ArrayList<Long[]>();
        for (Long[] origArr : this.requestAndResponseTimes){
            Long[] newArr = Arrays.copyOf(origArr, origArr.length);
            newList.add(newArr);
        }
        return newList;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.numIterations; i++) {
            Long getStartTime = System.currentTimeMillis();
            Object getResponse = this.client.getIt();
            Long getEndTime = System.currentTimeMillis();
            Long getResponseTime = getEndTime - getStartTime;
            synchronized (this) {
                Long[] arr = {getStartTime, getResponseTime};
                this.requestAndResponseTimes.add(arr);
                this.numRequestsSent++;
                if (getResponse.equals("Hi there!")){
                    this.numResponsesSuccessful++;
                }
            }
            Long postStartTime = System.currentTimeMillis();
            Object postResponse = this.client.postText("hello");
            Long postEndTime = System.currentTimeMillis();
            Long postResponseTime = postEndTime - postStartTime;
            synchronized (this) {
                Long[] arr = {postStartTime, postResponseTime};
                this.requestAndResponseTimes.add(arr);
                this.numRequestsSent++;
                if (postResponse.equals("Content length: 5")) {
                    this.numResponsesSuccessful++;
                }
            }
        }
        this.latch.countDown();
    }

}
