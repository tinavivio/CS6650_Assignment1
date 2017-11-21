package Client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;

public class JerseyClient {

    private final Client client;
    private final WebTarget webTarget;

    public JerseyClient(String ipAddress, String portNumber) {
        client = ClientBuilder.newClient();
        webTarget = client.target("http://" + ipAddress + ":" + portNumber + "/Assignment1_Vivio/rest");
    }
    
    public Response postNewLiftRide(String resortId, String dayNumber, String time, String skierId, String liftNumber) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("liftRides/load/{0},{1},{2},{3},{4}", new Object[]{resortId, dayNumber, time, skierId, liftNumber})).request(MediaType.TEXT_PLAIN).post(null, Response.class);
    }
    
    public <T> T getAllLiftRides(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("liftRides");
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T getAllLiftRidesBySkierIdAndDay(Class<T> responseType, String skierId, String dayNumber) throws ClientErrorException {
        WebTarget resource = this.webTarget.path(MessageFormat.format("liftRides/{0},{1}", new Object[]{skierId, dayNumber}));
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T getNumLiftRides(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("liftRides/count");
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public Response deleteAllLiftRidesByDay(String dayNumber) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("liftRides/delete/{0}", new Object[]{dayNumber})).request(MediaType.TEXT_PLAIN).delete(Response.class);
    }
    
    public Response postNewSkiDayStats(String skierId, String dayNumber, String numRides, String totalVertical) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("skiers/load/{0},{1},{2},{3}", new Object[]{skierId, dayNumber, numRides, totalVertical})).request(MediaType.TEXT_PLAIN).post(null, Response.class);
    }
    
    public <T> T getSkiDayStatsBySkierIdAndDay(Class<T> responseType, String skierId, String dayNumber) throws ClientErrorException {
        WebTarget resource = this.webTarget;
        resource = resource.path(MessageFormat.format("skiers/myVert/{0},{1}", new Object[]{skierId, dayNumber}));
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public Response deleteAllSkiDayStatsByDay(String dayNumber) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("skiers/delete/{0}", new Object[]{dayNumber})).request(MediaType.TEXT_PLAIN).delete(Response.class);
    }
    
    public Response deleteSkiDayStatsBySkierIdAndDay(String skierId, String dayNumber) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("skiers/delete/{0},{1}", new Object[]{skierId, dayNumber})).request(MediaType.TEXT_PLAIN).delete(Response.class);
    }
    
    public <T> T getAllSkiDayStatsByDay(Class<T> responseType, String dayNumber) throws ClientErrorException {
        WebTarget resource = this.webTarget;
        resource = resource.path(MessageFormat.format("skiers/{0}", new Object[]{dayNumber}));
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T getNumSkiDayStats(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("skiers/count");
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public Response postNewLift(String liftNumber, String height) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("lifts/load/{0},{1}", new Object[]{liftNumber, height})).request(MediaType.TEXT_PLAIN).post(null, Response.class);
    }
    
    public <T> T getLiftHeightByLiftNumber(Class<T> responseType, String liftNumber) throws ClientErrorException {
        WebTarget resource = this.webTarget;
        resource = resource.path(MessageFormat.format("lifts/height/{0}", new Object[]{liftNumber}));
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public Response deleteLiftByLiftNumber(String liftNumber) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("lifts/delete/{0}", new Object[]{liftNumber})).request(MediaType.TEXT_PLAIN).delete(Response.class);
    }
    
    public Response updateLiftByLiftNumber(String liftNumber, String height) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("lifts/update/{0},{1}", new Object[]{liftNumber, height})).request(MediaType.TEXT_PLAIN).put(null, Response.class);
    }
    
    public <T> T getAllLifts(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("lifts");
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T getNumLifts(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("lifts/count");
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public Response postNewMetrics(String serverId, String databaseRequestTime, String databaseResponseTime, String serverRequestTime, String serverResponseTime, String responseCode) throws ClientErrorException {
        return this.webTarget.path(MessageFormat.format("metrics/load/{0},{1},{2},{3},{4},{5}", new Object[]{serverId, databaseRequestTime, databaseResponseTime, serverRequestTime, serverResponseTime, responseCode})).request(MediaType.TEXT_PLAIN).post(null, Response.class);
    }
    
    public <T> T getAllMetrics(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("metrics");
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T getMetricsByServerId(Class<T> responseType, String serverId) throws ClientErrorException {
        WebTarget resource = this.webTarget.path(MessageFormat.format("metrics/{0}", new Object[]{serverId}));
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T getNumMetrics(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("metrics/count");
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public <T> T getMinDatabaseRequestTime(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("metrics/minDatabaseRequestTime");
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public <T> T getMinServerRequestTime(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = this.webTarget.path("metrics/minServerRequestTime");
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public <T> T getMinDatabaseRequestTimeByServerId(Class<T> responseType, String serverId) throws ClientErrorException {
        WebTarget resource = this.webTarget.path(MessageFormat.format("metrics/minDatabaseRequestTime/{0}", new Object[]{serverId}));
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public <T> T getMinServerRequestTimeByServerId(Class<T> responseType, String serverId) throws ClientErrorException {
        WebTarget resource = this.webTarget.path(MessageFormat.format("metrics/minServerRequestTime/{0}", new Object[]{serverId}));
        return resource.request(MediaType.TEXT_PLAIN).get(responseType);
    }
    
    public Response deleteAllMetrics() throws ClientErrorException {
        return this.webTarget.path("metrics/delete").request(MediaType.TEXT_PLAIN).delete(Response.class);
    }

    public void close() {
        this.client.close();
    }

}
