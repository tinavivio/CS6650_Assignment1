package Server.DAO;

import Server.Model.SkiDayStats;
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

@Repository("skiDayStatsDAO")
public class SkiDayStatsDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Transactional
    public long insertNewSkiDayStats(SkiDayStats skiDayStats) {
        if (this.getSkiDayStatsBySkierIdAndDay(skiDayStats.getSkierId(), skiDayStats.getDayNumber()) == null){
            long statsId = (Long) this.sessionFactory.getCurrentSession().save(skiDayStats);
            return statsId;
        }else{
            return -1;
        }
    }
    
    @Transactional
    public SkiDayStats getSkiDayStatsBySkierIdAndDay(int skierId, int dayNumber){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from SkiDayStats where skierId = :skierId and dayNumber = :dayNumber");
        query.setParameter("skierId", skierId);
        query.setParameter("dayNumber", dayNumber);
        List<SkiDayStats> list = query.getResultList();
        if(!list.isEmpty()){
            return list.get(0);
        }else{
            return null;
        }
    }
    
    @Transactional
    public int deleteAllSkiDayStatsByDay(int dayNumber){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from SkiDayStats where dayNumber = :dayNumber");
        query.setParameter("dayNumber", dayNumber);
        int rowsAffected = query.executeUpdate();
        return rowsAffected;
    }
    
    @Transactional
    public int deleteSkiDayStatsBySkierIdAndDay(int skierId, int dayNumber){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from SkiDayStats where skierId = :skierId and dayNumber = :dayNumber");
        query.setParameter("skierId", skierId);
        query.setParameter("dayNumber", dayNumber);
        int rowsAffected = query.executeUpdate();
        return rowsAffected;
    }
    
    @Transactional
    public List<SkiDayStats> getAllSkiDayStats(){
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<SkiDayStats> criteriaQuery = criteriaBuilder.createQuery(SkiDayStats.class);
        Root<SkiDayStats> from = criteriaQuery.from(SkiDayStats.class);
        CriteriaQuery<SkiDayStats> select = criteriaQuery.select(from);
        TypedQuery<SkiDayStats> typedQuery = session.createQuery(select);
        List<SkiDayStats> stats = typedQuery.getResultList();
        return stats;
    }
    
    @Transactional
    public List<Object[]> getAllSkiDayStatsByDay(int dayNumber) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("select a.skierId, count(*), sum(b.height) from LiftRide a, Lift b where a.liftNumber = b.liftNumber and a.dayNumber = :dayNumber group by a.skierId");
        query.setParameter("dayNumber", dayNumber);
        List<Object[]> resultList = query.getResultList();
        return resultList;
    }
    
}
