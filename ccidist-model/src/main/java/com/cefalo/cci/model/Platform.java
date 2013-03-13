package com.cefalo.cci.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "platform")
@Cache(region = "com.cefalo.cci.model.Platform", usage = CacheConcurrencyStrategy.READ_ONLY)
public class Platform  extends Persistent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;

    public Platform() {

    }

    public Platform(String id) {
        this.id = id;
    }

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
