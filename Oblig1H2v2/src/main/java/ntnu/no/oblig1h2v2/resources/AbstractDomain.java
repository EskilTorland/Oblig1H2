/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnu.no.oblig1h2v2.resources;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 *
 * @author eskil
 */
@MappedSuperclass
public abstract class AbstractDomain implements Serializable {
    
    @Column(nullable = false)
    @Version
    Timestamp version;
    
    @Column(nullable = false, updatable = false)
    private Timestamp created;
    
    public AbstractDomain() {
        this.created = new Timestamp(System.currentTimeMillis());
        this.version = new Timestamp(System.currentTimeMillis());
    }

    /**
     * @Version allows the JPA engine to use optimistic locking in the database.
     * JPA will update the timestamp on insert and update requests
     * @return 
     */
    public Timestamp getVersion() {
        return version;
    }

    public Timestamp getCreated() {
        return created;
    }
}
