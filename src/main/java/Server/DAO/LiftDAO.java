package Server.DAO;

import Server.Model.Lift;
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

@Repository("liftDAO")
public class LiftDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Transactional
    public long insertNewLift(Lift lift) {
        if (this.getLiftByLiftNumber(lift.getLiftNumber()) == null){
            long liftId = (Long) this.sessionFactory.getCurrentSession().save(lift);
            return liftId;
        }else{
            return -1;
        }
    }
    
    @Transactional
    public Lift getLiftByLiftNumber(int liftNumber){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Lift where liftNumber = :liftNumber");
        query.setParameter("liftNumber", liftNumber);
        List<Lift> list = query.getResultList();
        if(!list.isEmpty()){
            return list.get(0);
        }else{
            return null;
        }
    }
    
    @Transactional
    public int deleteLiftByLiftNumber(int liftNumber){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from Lift where liftNumber = :liftNumber");
        query.setParameter("liftNumber", liftNumber);
        int rowsAffected = query.executeUpdate();
        return rowsAffected;
    }
    
    @Transactional
    public int updateLiftByLiftNumber(int liftNumber, int height){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("update Lift set height = :height where liftNumber = :liftNumber");
        query.setParameter("liftNumber", liftNumber);
        query.setParameter("height", height);
        int rowsAffected = query.executeUpdate();
        return rowsAffected;
    }
    
    @Transactional
    public List<Lift> getAllLifts() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Lift> criteriaQuery = criteriaBuilder.createQuery(Lift.class);
        Root<Lift> from = criteriaQuery.from(Lift.class);
        CriteriaQuery<Lift> select = criteriaQuery.select(from);
        TypedQuery<Lift> typedQuery = session.createQuery(select);
        List<Lift> lifts = typedQuery.getResultList();
        return lifts;
    }
    
}
