package Client.PostClient;

import Client.JerseyClient;
import Client.ClientMetricsProcessor;
import Client.UpdateSkierStatsClient.UpdateSkierStatsClient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PostSkierDataClient {
    
    private final JerseyClient client;
    private final int dayNumber;
    private final String csvFile;
    private final ConcurrentMap<String, Long[]> metrics;
    private final CountDownLatch countDownLatch;

    public PostSkierDataClient(JerseyClient client, int dayNumber, String csvFile, ConcurrentMap<String, Long[]> metrics, CountDownLatch countDownLatch) {
        this.client = client;
        this.dayNumber = dayNumber;
        this.csvFile = csvFile;
        this.metrics = metrics;
        this.countDownLatch = countDownLatch;
    }
    
    public void runClient() {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";  
        // Instantiate a new Executor with a fixed size thread pool which will manage submission of runnable tasks.
        Executor exec = Executors.newFixedThreadPool(200);
        int count = -1;
        
        try {
            
            br = new BufferedReader(new FileReader(this.csvFile));
            System.out.println("===Reading csv file contents");
            while ((line = br.readLine()) != null) {
                
                if (count >= 0) {
                    
                    // Split each line at the commas, and submit a new task to the Executor with the details from the record.
                    // Also pass each runnable task the ConcurrentHashMap, CountDownLatch, and JerseyClient.
                    
                    String[] RFIDLiftData = line.split(cvsSplitBy);
                    
                    System.out.println("Reading Ski Data [Resort ID = " + RFIDLiftData[0] + ", Day = " + RFIDLiftData[1] + ", Skier ID = " + RFIDLiftData[2] + ", Lift ID = " + RFIDLiftData[3] + ", Time = " + RFIDLiftData[4] + "]");
                    
                    exec.execute(new PostSkierDataClientRunnable(RFIDLiftData[0], RFIDLiftData[1], RFIDLiftData[4], RFIDLiftData[2], RFIDLiftData[3], this.client, this.metrics, this.countDownLatch));
                    
                }
                
                count++;

            }
        
            System.out.println("Record Count = " + count);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    public static void main(String[] args) {
        String ipAddress = args[0];
        String portNumber = args[1];
        int dayNumber = Integer.parseInt(args[2]);
        String csvFile = args[3];
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        // Instantiate a new Executor with a fixed size thread pool which will manage submission of runnable tasks.
        Executor exec = Executors.newFixedThreadPool(200);
        int count = -1;
        // Instantiate a ConcurrentHashMap to track response times. 
        // Each key is a unique string, each value is an array of Long to track request sent time, response time,
        // and a boolean representing whether the response was successful or not.
        ConcurrentMap<String, Long[]> metrics = new ConcurrentHashMap<>();
        // Instantiate a CountDownLatch with an initial count of 800,000 (based on the number of records in the CSV file).
        CountDownLatch countDownLatch = new CountDownLatch(800000);
        Long testStartTime = System.currentTimeMillis();
        System.out.println("Client starting....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        // Instantiate a new JerseyClient which has methods that are able to send requests to the server.
        JerseyClient client = new JerseyClient(ipAddress, portNumber);
        
        try {
            
            br = new BufferedReader(new FileReader(csvFile));
            System.out.println("===Reading csv file contents");
            while ((line = br.readLine()) != null) {
                
                if (count >= 0) {
                    
                    // Split each line at the commas, and submit a new task to the Executor with the details from the record.
                    // Also pass each runnable task the ConcurrentHashMap, CountDownLatch, and JerseyClient.
                    
                    String[] RFIDLiftData = line.split(cvsSplitBy);
                    
                    System.out.println("Reading Ski Data [Resort ID = " + RFIDLiftData[0] + ", Day = " + RFIDLiftData[1] + ", Skier ID = " + RFIDLiftData[2] + ", Lift ID = " + RFIDLiftData[3] + ", Time = " + RFIDLiftData[4] + "]");
                    
                    exec.execute(new PostSkierDataClientRunnable(RFIDLiftData[0], RFIDLiftData[1], RFIDLiftData[4], RFIDLiftData[2], RFIDLiftData[3], client, metrics, countDownLatch));
                    
                }
                
                count++;

            }
            
            // Wait for the CountDownLatch to reach zero before continuing in the program.
            // This implies that all of the 800,000 records have been persisted to the database
            // (unless responses were unsuccessful) and it is safe to now calculate statistics for each skier.
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                System.err.println("Main thread interrupted!");
            }
        
            System.out.println("Record Count = " + count);
            
            // Instantiate a new CountDownLatch with an initial count of 40,000 (based on the number of skiers).
            CountDownLatch updateCountDownLatch = new CountDownLatch(40000);
            // Instantiate a new UpdateSkierStatsClient and pass it the CountDownLatch and JerseyClient.
            UpdateSkierStatsClient updateClient = new UpdateSkierStatsClient(dayNumber, updateCountDownLatch, client);
            // Call the UpdateSkierStatsClient's method that peforms the updates to the database.
            updateClient.updateSkierStats();
            // Wait for the CountDownLatch to reach zero before continuing in the program.
            // This implies that all of the 40,000 skiers' statistics have been updated and persisted to the database
            // (unless responses were unsuccessful).
            try {
                updateCountDownLatch.await();
            } catch (InterruptedException ex) {
                System.err.println("Main thread interrupted!");
            }
            
            System.out.println("Test complete....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
            // Close the JerseyClient and process the response time information that has been collected in the ConcurrentHashMap.
            client.close();
            Long testEndTime = System.currentTimeMillis();
            Long testWallTime = testEndTime - testStartTime;
            System.out.println("Test wall time: " + testWallTime.toString() + " milliseconds");
            Collection<Long[]> requestAndResponseTimes = metrics.values();
            ClientMetricsProcessor processor = new ClientMetricsProcessor(testWallTime, testStartTime, requestAndResponseTimes);
            processor.processLatencies();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
}
