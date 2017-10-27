package Client.GetClient;

import Client.JerseyClient;
import Client.Processor;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class GetSkierDataClient {
    
    public static void main(String[] args) {
        
        String ipAddress = args[0];
        String portNumber = args[1];
        int dayNumber = Integer.parseInt(args[2]);
        // Instantiate a ConcurrentHashMap to track response times. 
        // Each key is a skier ID, each key is an array of Long to track request sent time, response time,
        // and a boolean representing whether the response was successful or not.
        ConcurrentMap<Long, Long[]> metrics = new ConcurrentHashMap<>();
        // Instantiate a CountDownLatch with an initial count of 40,000 (based on the number of skiers).
        CountDownLatch countDownLatch = new CountDownLatch(40000);
        Long testStartTime = System.currentTimeMillis();
        System.out.println("Client starting....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        // Instantiate a new JerseyClient which has methods that are able to send requests to the server.
        JerseyClient client = new JerseyClient(ipAddress, portNumber);
        // Partition the 40,000 skier IDs into 100 chunks of 400 IDs each,
        // and start a new thread responsible for processing each chunk.
        // Pass each thread the range of IDs it is responsible for, the ConcurrentHashMap, CountDownLatch, and JerseyClient.
        for (int i = 1; i <= 100; i++) {
            int beginIndex = (400 * i) - 399;
            int endIndex = 400 * i;
            (new Thread(new GetSkierDataClientRunnableLevel1(dayNumber, beginIndex, endIndex, client, metrics, countDownLatch))).start();
        }
        System.out.println("All threads running....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        // Wait for the CountDownLatch to reach zero before continuing in the program.
        // This implies that all of the 40,000 skiers' statistics have been retrieved from the database
        // (unless responses were unsuccessful).
        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            System.err.println("Main thread interrupted!");
        }
        System.out.println("All threads complete....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        // Close the JerseyClient and process the response time information that has been collected in the ConcurrentHashMap.
        client.close();
        Long testEndTime = System.currentTimeMillis();
        Long testWallTime = testEndTime - testStartTime;
        System.out.println("Test wall time: " + testWallTime.toString() + " milliseconds");
        Collection<Long[]> requestAndResponseTimes = metrics.values();
        Processor processor = new Processor(testStartTime, requestAndResponseTimes);
        processor.processLatencies(); 

    }
}
