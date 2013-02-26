package com.cefalo.cci.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "publication")
public class Publication extends Persistent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long id;
    private String name;
    private Organization organization;
    private Set<PublicationPlatform> publicationPlatformSet;

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

    @ManyToOne
    @JoinColumn(name = "organization_id")
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public Set<PublicationPlatform> getPublicationPlatformSet() {
        return publicationPlatformSet;
    }

    public void setPublicationPlatformSet(Set<PublicationPlatform> publicationPlatformSet) {
        this.publicationPlatformSet = publicationPlatformSet;
    }
}
