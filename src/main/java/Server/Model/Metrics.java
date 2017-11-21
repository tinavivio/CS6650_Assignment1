package Server.Model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="metrics")
public class Metrics implements Serializable {
    
    @Id
    @GeneratedValue
    @Column(name="metricsId")
    private long metricsId;
    
    @Column(name="serverId")
    private String serverId;
    
    @Column(name="databaseRequestTime")
    private Long databaseRequestTime;
    
    @Column(name="databaseResponseTime")
    private Long databaseResponseTime;
    
    @Column(name="serverRequestTime")
    private Long serverRequestTime;
    
    @Column(name="serverResponseTime")
    private Long serverResponseTime;
    
    @Column(name="responseCode")
    private Long responseCode;

    public Metrics() {
        
    }
    
    public Metrics(String serverId, Long databaseRequestTime, Long databaseResponseTime, Long serverRequestTime, Long serverResponseTime, Long responseCode){
        this.serverId = serverId;
        this.databaseRequestTime = databaseRequestTime;
        this.databaseResponseTime = databaseResponseTime;
        this.serverRequestTime = serverRequestTime;
        this.serverResponseTime = serverResponseTime;
        this.responseCode = responseCode;
    }

    public long getMetricsId() {
        return this.metricsId;
    }

    public void setMetricsId(long metricsId) {
        this.metricsId = metricsId;
    }
    
    public String getServerId(){
        return this.serverId;
    }
    
    public void setServerId(String serverId){
        this.serverId = serverId;
    }
    
    public Long getDatabaseRequestTime() {
        return this.databaseRequestTime;
    }

    public void setDatabaseRequestTime(Long databaseRequestTime) {
        this.databaseRequestTime = databaseRequestTime;
    }

    public Long getDatabaseResponseTime() {
        return this.databaseResponseTime;
    }

    public void setDatabaseResponseTime(Long databaseResponseTime) {
        this.databaseResponseTime = databaseResponseTime;
    }
    
    public Long getServerRequestTime() {
        return this.serverRequestTime;
    }

    public void setServerRequestTime(Long serverRequestTime) {
        this.serverRequestTime = serverRequestTime;
    }

    public Long getServerResponseTime() {
        return this.serverResponseTime;
    }

    public void setServerResponseTime(Long serverResponseTime) {
        this.serverResponseTime = serverResponseTime;
    }

    public Long getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(Long responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "Metrics{" + "serverId=" + this.serverId + ", databaseRequestTime=" + this.databaseRequestTime + ", databaseResponseTime=" + this.databaseResponseTime + ", serverRequestTime=" + this.serverRequestTime + ", serverResponseTime=" + this.serverResponseTime + ", responseCode=" + this.responseCode + "}";
    }
    
    
    
}
