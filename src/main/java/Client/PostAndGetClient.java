package Client;

import Client.GetClient.GetSkierDataClient;
import Client.PostClient.PostSkierDataClient;
import Client.UpdateSkierStatsClient.UpdateSkierStatsClient;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class PostAndGetClient {
    
    public static void main(String[] args){
        String ipAddress = "DistributedSystems-1073193704.us-west-2.elb.amazonaws.com";
        String portNumber = "8080";
        // Instantiate a new JerseyClient which has methods that are able to send requests to the server.
        JerseyClient client = new JerseyClient(ipAddress, portNumber);
        // Instantiate a ConcurrentHashMap to track response times. 
        // Each key is a unique string, each value is an array of Long to track request sent time, response time,
        // and a boolean representing whether the response was successful or not.
        ConcurrentMap<String, Long[]> metrics = new ConcurrentHashMap<>();
        Long testStartTime = System.currentTimeMillis();
        // For each of days 3 through 5...
        for (int i = 3; i <= 5; i++){
            // Instantiate a CountDownLatch with an initial count of 200,000 (based on the number of records in the CSV file).
            CountDownLatch postCountDownLatch = new CountDownLatch(200000);
            String csvFile = "BSDSAssignment2Day" + i + ".csv";
            // Instantiate a PostSkierDataClient and call its runClient method, passing it the JerseyClient, day number, CSV file name,
            // ConcurrentHashMap, and CountDownLatch. 
            // This method does the work of posting all records from the given file to the server.
            PostSkierDataClient postClient = new PostSkierDataClient(client, i, csvFile, metrics, postCountDownLatch);
            postClient.runClient();
            // Wait for the CountDownLatch to reach zero before continuing in the program.
            // This implies that all of the 200,000 records have been persisted to the database
            // (unless responses were unsuccessful) and it is safe to now calculate statistics for each skier.
            try {
                postCountDownLatch.await();
            } catch (InterruptedException ex) {
                System.err.println("Main thread interrupted!");
            }
            // Instantiate a new CountDownLatch with an initial count of 10,000 (based on the number of skiers).
            CountDownLatch updateCountDownLatch = new CountDownLatch(10000);
            // Instantiate an UpdateSkierStatsClient and call its updateClient method, passing it the day number,
            // CountDownLatch, and JerseyClient. 
            // This method does the work of updating all skiers' statistics for the given day in the database.
            UpdateSkierStatsClient updateClient = new UpdateSkierStatsClient(i, updateCountDownLatch, client);
            updateClient.updateSkierStats();
            // Wait for the CountDownLatch to reach zero before continuing in the program.
            // This implies that all of the 10,000 skiers' statistics have been updated and persisted to the database
            // (unless responses were unsuccessful).
            try {
                updateCountDownLatch.await();
            } catch (InterruptedException ex) {
                System.err.println("Main thread interrupted!");
            }
            // Instantiate a new CountDownLatch with an initial count of 10,000 (based on the number of skiers).
            CountDownLatch getCountDownLatch = new CountDownLatch(10000);
            // Instantiate a GetSkierDataClient and call its runClient method, passing it the JerseyClient, day number,
            // ConcurrentHashMap, and CountDownLatch. 
            // This method does the work of getting all skiers' statistics for the given day from the server.
            GetSkierDataClient getClient = new GetSkierDataClient(client, i, metrics, getCountDownLatch);
            getClient.runClient();
            // Wait for the CountDownLatch to reach zero before continuing in the program.
            // This implies that all of the 10,000 skiers' statistics have been retrieved from the database
            // (unless responses were unsuccessful).
            try {
                getCountDownLatch.await();
            } catch (InterruptedException ex) {
                System.err.println("Main thread interrupted!");
            }
        }
        // Close the JerseyClient and process the response time information that has been collected in the ConcurrentHashMap.
        client.close();
        Long testEndTime = System.currentTimeMillis();
        Long testWallTime = testEndTime - testStartTime;
        System.out.println("Test wall time: " + testWallTime.toString() + " milliseconds");
        Collection<Long[]> requestAndResponseTimes = metrics.values();
        ClientMetricsProcessor processor = new ClientMetricsProcessor(testWallTime, testStartTime, requestAndResponseTimes);
        processor.processLatencies();
    }
    
}
