/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnu.no.oblig1h2v2.resources;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author eskil
 */
@Singleton
@Startup
public class RunOnStartup {
    @PersistenceContext
    EntityManager em;

    @Inject
    FantService fantService;
    
    @PostConstruct
    public void init() {
        System.out.println("Wrrooom! " + new Date());
        long groups = (long) em.createQuery("SELECT count(g.name) from Group g").getSingleResult();
        if(groups == 0) {
            em.persist(new Group(Group.USER));
            em.persist(new Group(Group.ADMIN));
        }
        
       
    }
}
