package com.beaconfire.coreservice.dao;

import com.beaconfire.coreservice.domain.Watchlist;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class WatchlistDao {
    private final SessionFactory sessionFactory;
    public WatchlistDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Watchlist> findActiveByUserId(Long userId){
        return sessionFactory.getCurrentSession().createQuery("from Watchlist where userId = :userId AND isActive=true", Watchlist.class)
                .setParameter("userId", userId)
                .list();
    }

    public Optional<Watchlist> findById(Long watchlistId){
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Watchlist.class, watchlistId));
    }

    public Watchlist saveOrUpdate(Watchlist watchlist){
        return sessionFactory.getCurrentSession().merge(watchlist);
    }

    public void persist(Watchlist watchlist){
        sessionFactory.getCurrentSession().persist(watchlist);
    }


}
