package Server.DAO;

import Server.Model.Metrics;
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

@Repository("metricsDAO")
public class MetricsDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Transactional
    public long insertNewMetrics(Metrics metrics) {
        long metricsId = (Long) this.sessionFactory.getCurrentSession().save(metrics);
        return metricsId;   
    }
    
    @Transactional
    public int insertNewBatchOfMetrics(List<Metrics> metrics){
        metrics.forEach((m) -> {
            this.sessionFactory.getCurrentSession().save(m);
        });
        return metrics.size();
    }
    
    @Transactional
    public List<Metrics> getAllMetrics() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Metrics> criteriaQuery = criteriaBuilder.createQuery(Metrics.class);
        Root<Metrics> from = criteriaQuery.from(Metrics.class);
        CriteriaQuery<Metrics> select = criteriaQuery.select(from);
        TypedQuery<Metrics> typedQuery = session.createQuery(select);
        List<Metrics> metrics = typedQuery.getResultList();
        return metrics;
    }
    
    @Transactional
    public List<Metrics> getMetricsByServerId(String serverId) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Metrics where serverId = :serverId");
        query.setParameter("serverId", serverId);
        List<Metrics> list = query.getResultList();
        return list;
    }
    
    @Transactional
    public int deleteAllMetrics(){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from Metrics");
        int rowsAffected = query.executeUpdate();
        return rowsAffected;
    }
    
    @Transactional
    public Long getMinServerRequestTime(){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Metrics order by serverRequestTime");
        query.setMaxResults(1);
        List<Metrics> list = query.getResultList();
        return list.get(0).getServerRequestTime();  
    }
    
    @Transactional
    public Long getMinDatabaseRequestTime(){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Metrics order by databaseRequestTime");
        query.setMaxResults(1);
        List<Metrics> list = query.getResultList();
        return list.get(0).getDatabaseRequestTime();   
    }
    
    @Transactional
    public Long getMinServerRequestTimeByServerId(String serverId){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Metrics where serverId = :serverId order by serverRequestTime");
        query.setParameter("serverId", serverId);
        query.setMaxResults(1);
        List<Metrics> list = query.getResultList();
        return list.get(0).getServerRequestTime();  
    }
    
    @Transactional
    public Long getMinDatabaseRequestTimeByServerId(String serverId){
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Metrics where serverId = :serverId order by databaseRequestTime");
        query.setParameter("serverId", serverId);
        query.setMaxResults(1);
        List<Metrics> list = query.getResultList();
        return list.get(0).getDatabaseRequestTime();   
    }
    
}
