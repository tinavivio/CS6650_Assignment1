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
        ConcurrentMap<Long, Long[]> metrics = new ConcurrentHashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(40000);
        Long testStartTime = System.currentTimeMillis();
        System.out.println("Client starting....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        JerseyClient client = new JerseyClient(ipAddress, portNumber);
        for (int i = 1; i <= 100; i++) {
            int beginIndex = (400 * i) - 399;
            int endIndex = 400 * i;
            (new Thread(new GetSkierDataClientRunnableLevel1(dayNumber, beginIndex, endIndex, client, metrics, countDownLatch))).start();
        }
        System.out.println("All threads running....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            System.out.println("Main thread interrupted!");
        }
        System.out.println("All threads complete....Time: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        client.close();
        Long testEndTime = System.currentTimeMillis();
        Long testWallTime = testEndTime - testStartTime;
        System.out.println("Test wall time: " + testWallTime.toString() + " milliseconds");
        Collection<Long[]> requestAndResponseTimes = metrics.values();
        Processor processor = new Processor(testStartTime, requestAndResponseTimes);
        processor.processLatencies(); 

    }
}
