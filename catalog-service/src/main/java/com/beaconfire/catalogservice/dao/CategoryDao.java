package com.beaconfire.catalogservice.dao;

import com.beaconfire.catalogservice.domain.Category;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Repository
public class CategoryDao {
    private final SessionFactory sessionFactory;
    public CategoryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Category> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Category", Category.class).list();
    }
}
