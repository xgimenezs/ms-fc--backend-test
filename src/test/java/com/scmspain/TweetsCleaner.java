package com.scmspain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * Created by xgimenez on 2/10/17.
 */
public class TweetsCleaner {

    @Autowired
    private EntityManager em;

    @Transactional
    public void clean() {
        em.createQuery("DELETE FROM Tweet").executeUpdate();
    }
}
