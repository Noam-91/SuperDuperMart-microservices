package com.beaconfire.userservice.dao;

import com.beaconfire.userservice.domain.User;
import com.beaconfire.userservice.domain.UserProfile;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserDao {
    private final SessionFactory sessionFactory;
    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** Find all active users with criteria
     * @return List<User>
     */
    @Transactional(readOnly = true)
    public List<User> findAllActive(){
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        Predicate activePredicate = builder.equal(root.get("isActive"), true);
        criteria.where(activePredicate);
        TypedQuery<User> query = session.createQuery(criteria);
        return query.getResultList();
    }

    public void save(User user) throws RuntimeException{
        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
    }

    public void save(UserProfile userProfile) throws RuntimeException{
        Session session = sessionFactory.getCurrentSession();
        session.persist(userProfile);
    }
}
