package Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClientMetricsProcessor {
    
    private final Long testWallTime;
    private final Long testStartTime;
    private final Collection<Long[]> requestAndResponseTimes;

    public ClientMetricsProcessor(Long testWallTime, Long testStartTime, Collection<Long[]> requestAndResponseTimes) {
        this.testWallTime = testWallTime;
        this.testStartTime = testStartTime;
        this.requestAndResponseTimes = requestAndResponseTimes;
    }
    
    public void processLatencies(){
        int numRequestsSent = this.requestAndResponseTimes.size();
        System.out.println("Total number of requests sent: " + numRequestsSent);
        // Instantiate a new List<Long> to store just the response times.
        // This is so that this list can be sorted separately to get median and percentiles easily.
        List<Long> latencies = new ArrayList<>();
        Long numResponsesSuccessful = new Long(0);
        // Instantiate a new HashMap to track data needed to calculate average response times per second.
        // After processing, this map will have a key for each second of the total test wall time.
        // The value is an array of Long that contains the number of requests sent in that second,
        // and the sum of response times for each request sent in that second.
        Map<Long, Long[]> perSecondStats = new HashMap<>();
        Iterator<Long[]> iterator = this.requestAndResponseTimes.iterator();
        while (iterator.hasNext()){
            Long[] arr = iterator.next();
            if (arr[1] != -1){
                // Add the response time to the list of response times (if the response was successful).
                latencies.add(arr[1]);
                // Determine which second the request was sent in based on the test start time,
                // and check if the hash map already contains that key.
                // If the hash map already contains the key, update the value.
                // Otherwise add the new key and value to the hash map.
                Long key = ((arr[0] - testStartTime) / 1000) + 1;
                if(perSecondStats.containsKey(key)){
                    Long[] oldValue = perSecondStats.get(key);
                    Long[] newValue = {oldValue[0] + 1, oldValue[1] + arr[1]};
                    perSecondStats.put(key, newValue);
                }else{
                    Long[] newValue = {new Long(1), arr[1]};
                    perSecondStats.put(key, newValue);
                }
            }
            // Update the number of successful responses.
            numResponsesSuccessful += arr[2];
        }
        System.out.println("Total number of successful responses: " + numResponsesSuccessful.toString());
        // Sort the list of response times so that the median and percentiles can be obtained.
        Long[] latenciesArray = latencies.toArray(new Long[0]);
        Arrays.sort(latenciesArray);
        // Calculate the sum of all response times so that mean response time can be obtained.
        Long totalResponseTimeForAllRequests = new Long(0);
        for (Long time : latenciesArray){
            totalResponseTimeForAllRequests += time;
        }
        Long medianResponseTime = latenciesArray[latenciesArray.length/2];
        Long meanResponseTime = totalResponseTimeForAllRequests / latenciesArray.length;
        Long ninetyFifthPercentile = latenciesArray[((int) (latenciesArray.length * 0.95))];
        Long ninetyNinthPercentile = latenciesArray[((int) (latenciesArray.length * 0.99))];
        Long throughput = numRequestsSent / (this.testWallTime / 1000);
        System.out.println("Mean response time: " + meanResponseTime.toString() + " milliseconds");
        System.out.println("Median response time: " + medianResponseTime.toString() + " milliseconds");
        System.out.println("95th percentile response time: " + ninetyFifthPercentile.toString() + " milliseconds");
        System.out.println("99th percentile response time: " + ninetyNinthPercentile.toString() + " milliseconds");
        System.out.println("Throughput: " + throughput.toString() + " requests processed per second");
        // Instantiate a new XYLineChart and pass it the per second stats. Delegate the charting to this module.
        ClientXYLineChart chart = new ClientXYLineChart(perSecondStats);
        chart.chartLatencies();
    }
    
}
