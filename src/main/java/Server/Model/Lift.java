package Server.Model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="lifts")
public class Lift implements Serializable {
    
    @Id
    @GeneratedValue
    @Column(name="liftId")
    private long liftId;
    
    @Column(name="liftNumber")
    private int liftNumber;
    
    @Column(name="height")
    private int height;

    public Lift() {
        
    }
    
    public Lift(int liftNumber, int height) {
        this.liftNumber = liftNumber;
        this.height = height;
    }

    public long getLiftId() {
        return this.liftId;
    }

    public void setLiftId(long liftId) {
        this.liftId = liftId;
    }
    
    public int getLiftNumber() {
        return this.liftNumber;
    }
    
    public void setLiftNumber(int liftNumber){
        this.liftNumber = liftNumber;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
}
