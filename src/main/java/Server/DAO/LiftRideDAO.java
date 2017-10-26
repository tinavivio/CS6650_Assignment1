package Server.DAO;

import Server.Model.LiftRide;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("liftRideDAO")
public class LiftRideDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Transactional
    public Long insertNewLiftRide(LiftRide liftRide) {
        long rideId = (Long) this.sessionFactory.getCurrentSession().save(liftRide);
        return rideId;
    }
    
    @Transactional
    public List<LiftRide> getAllLiftRides() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<LiftRide> criteriaQuery = criteriaBuilder.createQuery(LiftRide.class);
        Root<LiftRide> from = criteriaQuery.from(LiftRide.class);
        CriteriaQuery<LiftRide> select = criteriaQuery.select(from);
        TypedQuery<LiftRide> typedQuery = session.createQuery(select);
        List<LiftRide> liftRides = typedQuery.getResultList();
        return liftRides;
    }
    
    @Transactional
    public List<LiftRide> getAllLiftRidesBySkierIdAndDay(int skierId, int dayNumber){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from LiftRide where skierId = :skierId and dayNumber = :dayNumber");
        query.setParameter("skierId", skierId);
        query.setParameter("dayNumber", dayNumber);
        List<LiftRide> list = query.getResultList();
        return list;
    }
    
    @Transactional
    public int deleteAllLiftRidesByDay(int dayNumber){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from LiftRide where dayNumber = :dayNumber");
        query.setParameter("dayNumber", dayNumber);
        int rowsAffected = query.executeUpdate();
        return rowsAffected;
    }
    
}
