package com.cefalo.cci.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "organization")
public class Organization  extends Persistent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long id;
    private String name;
    private Set<Publication> publicationSet;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization")
    public Set<Publication> getPublicationSet() {
        return publicationSet;
    }

    public void setPublicationSet(Set<Publication> publicationSet) {
        this.publicationSet = publicationSet;
    }
}
