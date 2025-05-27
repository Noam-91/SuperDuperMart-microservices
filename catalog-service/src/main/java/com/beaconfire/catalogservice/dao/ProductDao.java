package com.beaconfire.catalogservice.dao;

import com.beaconfire.catalogservice.domain.Product;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class ProductDao {
    private final SessionFactory sessionFactory;
    public ProductDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Product> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Product", Product.class).list();
    }

    public List<Product> findAllActive() {
        return sessionFactory.getCurrentSession().createQuery("from Product where isActive = true", Product.class).list();
    }

    public Optional<Product> findById(Long productId) {
        Product product = sessionFactory.getCurrentSession().get(Product.class, productId);
        return Optional.ofNullable(product);
    }

    public List<Product> findAllByIds(List<Long> productIds){
        return sessionFactory.getCurrentSession().createQuery("from Product where productId in (:productIds)", Product.class)
                .setParameter("productIds", productIds).list();
    }

    public Product save(Product product) {
        return sessionFactory.getCurrentSession().merge(product);
    }
}
