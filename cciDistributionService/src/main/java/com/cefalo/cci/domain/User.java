package com.cefalo.cci.domain;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    private int id;
    private String name;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
