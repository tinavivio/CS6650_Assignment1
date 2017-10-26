package Server.Model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="rides")
public class LiftRide implements Serializable, Comparable<LiftRide> {
    
    @Id
    @GeneratedValue
    @Column(name="rideId")
    private long rideId;

    @Column(name="resortId")
    private int resortId;
    
    @Column(name="dayNumber")
    private int dayNumber;
    
    @Column(name="skierId")
    private int skierId;
    
    @Column(name="liftNumber")
    private int liftNumber;
    
    @Column(name="time")
    private int time;
    
    public LiftRide() {
    
    }
    
    public LiftRide(int resortId, int dayNumber, int skierId, int liftNumber, int time) {
        this.resortId = resortId;
        this.dayNumber = dayNumber;
        this.skierId = skierId;
        this.liftNumber = liftNumber;
        this.time = time;
    }
    
    public long getRideId() {
	return this.rideId;
    }

    public void setRideId(long rideId) {
	this.rideId = rideId;
    }

    public int getResortId() {
        return this.resortId;
    }

    public void setResortId(int resortId) {
        this.resortId = resortId;
    }

    public int getDayNumber() {
        return this.dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getSkierId() {
        return this.skierId;
    }

    public void setSkierId(int skierId) {
        this.skierId = skierId;
    }

    public int getLiftNumber() {
        return this.liftNumber;
    }

    public void setLiftNumber(int liftNumber) {
        this.liftNumber = liftNumber;
    }
    
    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
    @Override
    public int compareTo(LiftRide compareData) {
        int compareTime = ((LiftRide) compareData).getTime();
        
        //ascending order
        return this.time - compareTime ;
    }
    
}
