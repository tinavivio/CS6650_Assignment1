package Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Processor {
    
    private final Long testStartTime;
    private final Collection<Long[]> requestAndResponseTimes;

    public Processor(Long testStartTime, Collection<Long[]> requestAndResponseTimes) {
        this.testStartTime = testStartTime;
        this.requestAndResponseTimes = requestAndResponseTimes;
    }
    
    public void processLatencies(){
        int numRequestsSent = this.requestAndResponseTimes.size();
        System.out.println("Total number of requests sent: " + numRequestsSent);
        List<Long> latencies = new ArrayList<>();
        Long numResponsesSuccessful = new Long(0);
        Map<Long, Long[]> perSecondStats = new HashMap<>();
        Iterator<Long[]> iterator = this.requestAndResponseTimes.iterator();
        while (iterator.hasNext()){
            Long[] arr = iterator.next();
            if (arr[1] != -1){
                latencies.add(arr[1]);
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
            numResponsesSuccessful += arr[2];
        }
        System.out.println("Total number of successful responses: " + numResponsesSuccessful.toString());
        Long[] latenciesArray = latencies.toArray(new Long[0]);
        Arrays.sort(latenciesArray);
        Long totalResponseTimeForAllRequests = new Long(0);
        for (Long time : latenciesArray){
            totalResponseTimeForAllRequests += time;
        }
        Long medianResponseTime = latenciesArray[latenciesArray.length/2];
        Long meanResponseTime = totalResponseTimeForAllRequests / latenciesArray.length;
        Long ninetyFifthPercentile = latenciesArray[((int) (latenciesArray.length * 0.95))];
        Long ninetyNinthPercentile = latenciesArray[((int) (latenciesArray.length * 0.99))];
        System.out.println("Mean response time: " + meanResponseTime.toString() + " milliseconds");
        System.out.println("Median response time: " + medianResponseTime.toString() + " milliseconds");
        System.out.println("95th percentile response time: " + ninetyFifthPercentile.toString() + " milliseconds");
        System.out.println("99th percentile response time: " + ninetyNinthPercentile.toString() + " milliseconds");
        XYLineChart chart = new XYLineChart(perSecondStats);
        chart.chartLatencies();
    }
    
}
