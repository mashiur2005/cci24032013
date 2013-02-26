package com.cefalo.cci.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "platform")
public class Platform  extends Persistent implements Serializable {
    private long id;
    private String name;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
