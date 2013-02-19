package com.cefalo.cci.dao;

import com.cefalo.cci.domain.User;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;

public class ResourceDaoImpl implements ResourceDao {
    @Inject
    private EntityManager em;

    @Override
    @Transactional
    public void saveUserInfo(User user) {
        em.persist(user);

    }
}
