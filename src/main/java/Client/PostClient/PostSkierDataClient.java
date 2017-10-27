package Client.PostClient;

import Client.JerseyClient;
import Client.Processor;
import Client.UpdateSkierStatsClient.UpdateSkierStatsClient;
import Server.Model.LiftRide;
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
    
    public static void main(String[] args) {
        
        String ipAddress = args[0];
        String portNumber = args[1];
        int dayNumber = Integer.parseInt(args[2]);
        String csvFile = args[3]/*"/Users/tinavivio/NetBeansProjects/Assignment1_Vivio/BSDSAssignment2Day1.csv"*/;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        Executor exec = Executors.newFixedThreadPool(200);
        int count = -1;
        ConcurrentMap<LiftRide, Long[]> metrics = new ConcurrentHashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(800000);
        Long testStartTime = System.currentTimeMillis();
        System.out.println("Client starting....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        JerseyClient client = new JerseyClient(ipAddress, portNumber);
        
        try {
            
            br = new BufferedReader(new FileReader(csvFile));
            System.out.println("===Reading csv file contents");
            while ((line = br.readLine()) != null) {
                
                if (count >= 0) {
                    
                    String[] RFIDLiftData = line.split(cvsSplitBy);
                    
                    System.out.println("Reading Ski Data [Resort ID = " + RFIDLiftData[0] + ", Day = " + RFIDLiftData[1] + ", Skier ID = " + RFIDLiftData[2] + ", Lift ID = " + RFIDLiftData[3] + ", Time = " + RFIDLiftData[4] + "]");
                    
                    exec.execute(new PostSkierDataClientRunnable(RFIDLiftData[0], RFIDLiftData[1], RFIDLiftData[4], RFIDLiftData[2], RFIDLiftData[3], client, metrics, countDownLatch));
                    
                }
                
                count++;

            }
            
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                System.err.println("Main thread interrupted!");
            }
        
            System.out.println("Record Count = " + count);
            
            CountDownLatch updateCountDownLatch = new CountDownLatch(40000);
            UpdateSkierStatsClient updateClient = new UpdateSkierStatsClient(dayNumber, updateCountDownLatch, client);
            updateClient.updateSkierStats();
            try {
                updateCountDownLatch.await();
            } catch (InterruptedException ex) {
                System.err.println("Main thread interrupted!");
            }
            
            System.out.println("Test complete....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
            client.close();
            Long testEndTime = System.currentTimeMillis();
            Long testWallTime = testEndTime - testStartTime;
            System.out.println("Test wall time: " + testWallTime.toString() + " milliseconds");
            Collection<Long[]> requestAndResponseTimes = metrics.values();
            Processor processor = new Processor(testStartTime, requestAndResponseTimes);
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
