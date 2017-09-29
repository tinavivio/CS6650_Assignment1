package Client;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) {
        int numThreads = Integer.parseInt(args[0]);
        int numIterations = Integer.parseInt(args[1]);
        String ipAddress = args[2];
        String portNumber = args[3];
        Long testStartTime = System.currentTimeMillis();
        System.out.println("Client starting....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        JerseyClient client = new JerseyClient(ipAddress, portNumber);
        CountDownLatch latch = new CountDownLatch(numThreads);
        IterativeHttpRequester httpRequester = new IterativeHttpRequester(client, latch, numIterations);
        for (int i = 0; i < numThreads; i++) {
            (new Thread(httpRequester)).start();
        }
        System.out.println("All threads running....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        try {
            latch.await();
        } catch (InterruptedException ex) {
            System.out.println("Main thread interrupted!");
        }
        System.out.println("All threads complete....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        client.close();
        Long testEndTime = System.currentTimeMillis();
        Long testWallTime = testEndTime - testStartTime;
        int numRequestsSent = httpRequester.getNumRequestsSent();
        int numResponsesSuccessful = httpRequester.getNumResponsesSuccessful();
        List<Long[]> requestAndResponseTimes = httpRequester.getRequestAndResponseTimes();
        Long[] latencies = new Long[requestAndResponseTimes.size()];
        for (int i = 0; i < requestAndResponseTimes.size(); i++){
            latencies[i] = requestAndResponseTimes.get(i)[1];
        }
        Arrays.sort(latencies);
        Long totalResponseTimeForAllRequests = new Long(0);
        for (Long time : latencies){
            totalResponseTimeForAllRequests += time;
        }
        Long medianResponseTime = latencies[latencies.length/2];
        Long meanResponseTime = totalResponseTimeForAllRequests / numRequestsSent;
        Long ninetyFifthPercentile = latencies[((int) (latencies.length * 0.95))];
        Long ninetyNinthPercentile = latencies[((int) (latencies.length * 0.99))];
        System.out.println("Total number of requests sent: " + numRequestsSent);
        System.out.println("Total number of successful responses: " + numResponsesSuccessful);
        System.out.println("Test wall time: " + testWallTime.toString() + " milliseconds");
        System.out.println("Mean response time: " + meanResponseTime.toString() + " milliseconds");
        System.out.println("Median response time: " + medianResponseTime.toString() + " milliseconds");
        System.out.println("95th percentile response time: " + ninetyFifthPercentile.toString() + " milliseconds");
        System.out.println("99th percentile response time: " + ninetyNinthPercentile.toString() + " milliseconds");
    }

}
