package Server.Model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="stats")
public class SkiDayStats implements Serializable {
    
    @Id
    @GeneratedValue
    @Column(name="statsId")
    private long statsId;
    
    @Column(name="skierId")
    private int skierId;
    
    @Column(name="dayNumber")
    private int dayNumber;
    
    @Column(name="numRides")
    private int numRides;
    
    @Column(name="totalVertical")
    private int totalVertical;
    
    public SkiDayStats() {
    
    }
    
    public SkiDayStats(int skierId, int dayNumber, int numRides, int totalVertical) {
        this.skierId = skierId;
        this.dayNumber = dayNumber;
        this.numRides = numRides;
        this.totalVertical = totalVertical;
    }

    public long getStatsId() {
        return this.statsId;
    }

    public void setStatsId(long statsId) {
        this.statsId = statsId;
    }

    public int getSkierId() {
        return this.skierId;
    }

    public void setSkierId(int skierId) {
        this.skierId = skierId;
    }

    public int getDayNumber() {
        return this.dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getNumRides() {
        return this.numRides;
    }

    public void setNumRides(int numRides) {
        this.numRides = numRides;
    }

    public int getTotalVertical() {
        return this.totalVertical;
    }

    public void setTotalVertical(int totalVertical) {
        this.totalVertical = totalVertical;
    }

    @Override
    public String toString() {
        return "SkiDayStats{" + "statsId=" + this.statsId + ", skierId=" + this.skierId + ", dayNumber=" + this.dayNumber + ", numRides=" + this.numRides + ", totalVertical=" + this.totalVertical + '}';
    }
    
}
