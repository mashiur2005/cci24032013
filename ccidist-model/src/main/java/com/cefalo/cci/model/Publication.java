package com.cefalo.cci.model;

import java.io.Serializable;
import java.util.Set;

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

    private String id;
    private String name;
    private Organization organization;
    private Set<Platform> platforms;

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

    @ManyToOne
    @JoinColumn(name = "organization_id")
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @OneToMany
    @JoinTable(name = "publication_platform",
            joinColumns = {
                    @JoinColumn(name = "publication_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "platform_id")
            }
    )
    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<Platform> platforms) {
        this.platforms = platforms;
    }
}
