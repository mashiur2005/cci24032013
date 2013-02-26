package com.cefalo.cci.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "publication")
public class Publication extends Persistent implements Serializable {
    private long id;
    private String name;
    private Organization organization;
    private Set<PubtPlatform> publicationPlatformSet;

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
    @JoinTable(
            name = "publication_platform",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "pubt_platform_id")
    )
    public Set<PubtPlatform> getPublicationPlatformSet() {
        return publicationPlatformSet;
    }

    public void setPublicationPlatformSet(Set<PubtPlatform> publicationPlatformSet) {
        this.publicationPlatformSet = publicationPlatformSet;
    }
}
