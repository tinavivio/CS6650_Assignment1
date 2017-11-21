package Client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServerMetricsProcessor {
    
    private final Long[] databaseResponseTimes;
    private final Map<Long, Long[]> databasePerSecondStats;
    private final Long[] serverResponseTimes;
    private final Map<Long, Long[]> serverPerSecondStats;
    private final Long[] responseCodes;

    public ServerMetricsProcessor(Long[] databaseResponseTimes, Map<Long, Long[]> databasePerSecondStats, Long[] serverResponseTimes, Map<Long, Long[]> serverPerSecondStats, Long[] responseCodes) {
        this.databaseResponseTimes = databaseResponseTimes;
        this.databasePerSecondStats = databasePerSecondStats;
        this.serverResponseTimes = serverResponseTimes;
        this.serverPerSecondStats = serverPerSecondStats;
        this.responseCodes = responseCodes;
    }
    
    public void processLatencies(){
        int numRequestsSent = this.responseCodes.length;
        System.out.println("Total number of requests sent: " + numRequestsSent);
        int numResponsesSuccessful = 0;
        for (Long code : this.responseCodes){
            if (code == 200 || code == 201){
                numResponsesSuccessful++;
            }
        }
        System.out.println("Total number of successful responses: " + numResponsesSuccessful);
        Arrays.sort(this.databaseResponseTimes);
        Arrays.sort(this.serverResponseTimes);
        Long totalResponseTimeForAllDbRequests = new Long(0);
        for (Long time : this.databaseResponseTimes){
            totalResponseTimeForAllDbRequests += time;
        }
        Long totalResponseTimeForAllServerRequests = new Long(0);
        for (Long time : this.serverResponseTimes){
            totalResponseTimeForAllServerRequests += time;
        }
        Long medianDbResponseTime = this.databaseResponseTimes[this.databaseResponseTimes.length/2];
        Long meanDbResponseTime = totalResponseTimeForAllDbRequests / this.databaseResponseTimes.length;
        Long dbNinetyFifthPercentile = this.databaseResponseTimes[((int) (this.databaseResponseTimes.length * 0.95))];
        Long dbNinetyNinthPercentile = this.databaseResponseTimes[((int) (this.databaseResponseTimes.length * 0.99))];
        Long medianServerResponseTime = this.serverResponseTimes[this.serverResponseTimes.length/2];
        Long meanServerResponseTime = totalResponseTimeForAllServerRequests / this.serverResponseTimes.length;
        Long serverNinetyFifthPercentile = this.serverResponseTimes[((int) (this.serverResponseTimes.length * 0.95))];
        Long serverNinetyNinthPercentile = this.serverResponseTimes[((int) (this.serverResponseTimes.length * 0.99))];
        System.out.println("Mean DB response time: " + meanDbResponseTime.toString() + " milliseconds");
        System.out.println("Median DB response time: " + medianDbResponseTime.toString() + " milliseconds");
        System.out.println("DB 95th percentile response time: " + dbNinetyFifthPercentile.toString() + " milliseconds");
        System.out.println("DB 99th percentile response time: " + dbNinetyNinthPercentile.toString() + " milliseconds");
        System.out.println("Mean Server response time: " + meanServerResponseTime.toString() + " milliseconds");
        System.out.println("Median Server response time: " + medianServerResponseTime.toString() + " milliseconds");
        System.out.println("Server 95th percentile response time: " + serverNinetyFifthPercentile.toString() + " milliseconds");
        System.out.println("Server 99th percentile response time: " + serverNinetyNinthPercentile.toString() + " milliseconds");     
        // Instantiate a new XYLineChart and pass it the per second stats. Delegate the charting to this module.
        ServerXYLineChart chart = new ServerXYLineChart(databasePerSecondStats, serverPerSecondStats);
        chart.chartLatencies();
    }
    
    public static void main (String[] args) {
        //String ipAddress = args[0];
        //String portNumber = args[1];
        //String serverId = args[2];
        String ipAddress = "DistributedSystems-1073193704.us-west-2.elb.amazonaws.com";
        String portNumber = "8080";
        String serverId = "all";
        JerseyClient client = new JerseyClient(ipAddress, portNumber);
        Response getResponse;
        Response minDatabaseRequestTimeResponse;
        Response minServerRequestTimeResponse;
        if (serverId.equals("all")) {
            getResponse = client.getAllMetrics(Response.class);
            minDatabaseRequestTimeResponse = client.getMinDatabaseRequestTime(Response.class);
            minServerRequestTimeResponse = client.getMinServerRequestTime(Response.class);
        } else {
            getResponse = client.getMetricsByServerId(Response.class, serverId);
            minDatabaseRequestTimeResponse = client.getMinDatabaseRequestTimeByServerId(Response.class, serverId);
            minServerRequestTimeResponse = client.getMinServerRequestTimeByServerId(Response.class, serverId);
        }
        String responseAsString = getResponse.readEntity(String.class);
        getResponse.close();
        Long minDatabaseRequestTime = Long.parseLong(minDatabaseRequestTimeResponse.readEntity(String.class));
        minDatabaseRequestTimeResponse.close();
        Long minServerRequestTime = Long.parseLong(minServerRequestTimeResponse.readEntity(String.class));
        minServerRequestTimeResponse.close();
        JSONArray responseAsJson = new JSONArray(responseAsString);
        Long[] databaseResponseTimes = new Long[responseAsJson.length()];
        Long[] serverResponseTimes = new Long[responseAsJson.length()];
        Long[] responseCodes = new Long[responseAsJson.length()];
        Map<Long, Long[]> databasePerSecondStats = new HashMap<>();
        Map<Long, Long[]> serverPerSecondStats = new HashMap<>();
        for (int i = 0; i < responseAsJson.length(); i++) {
            JSONObject obj = (JSONObject) responseAsJson.get(i);
            Long databaseRequestTime = (Long) obj.get("databaseRequestTime");
            Long databaseResponseTime = new Long((Integer) obj.get("databaseResponseTime"));
            databaseResponseTimes[i] = databaseResponseTime;
            Long serverRequestTime = (Long) obj.get("serverRequestTime");
            Long serverResponseTime = new Long((Integer) obj.get("serverResponseTime"));
            serverResponseTimes[i] = serverResponseTime;
            responseCodes[i] = new Long((Integer) obj.get("responseCode"));
            Long dbKey = ((databaseRequestTime - minDatabaseRequestTime) / 1000) + 1;
            if(databasePerSecondStats.containsKey(dbKey)){
                Long[] oldValue = databasePerSecondStats.get(dbKey);
                Long[] newValue = {oldValue[0] + 1, oldValue[1] + databaseResponseTime};
                databasePerSecondStats.put(dbKey, newValue);
            }else{
                Long[] newValue = {new Long(1), databaseResponseTime};
                databasePerSecondStats.put(dbKey, newValue);
            }
            Long serverKey = ((serverRequestTime - minServerRequestTime) / 1000) + 1;
            if(serverPerSecondStats.containsKey(serverKey)){
                Long[] oldValue = serverPerSecondStats.get(serverKey);
                Long[] newValue = {oldValue[0] + 1, oldValue[1] + serverResponseTime};
                serverPerSecondStats.put(serverKey, newValue);
            }else{
                Long[] newValue = {new Long(1), serverResponseTime};
                serverPerSecondStats.put(serverKey, newValue);
            }
        } 
        ServerMetricsProcessor processor = new ServerMetricsProcessor(databaseResponseTimes, databasePerSecondStats, serverResponseTimes, serverPerSecondStats, responseCodes);
        processor.processLatencies();
    }
    
}
