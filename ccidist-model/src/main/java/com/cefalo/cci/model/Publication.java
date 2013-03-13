package com.cefalo.cci.model;

import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "publication")
@Cache(region = "com.cefalo.cci.model.Publication", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Publication extends Persistent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Organization organization;
    private Date created;
    private Date updated;
    private Set<Platform> platforms;

    public Publication() {

    }

    public Publication(String id) {
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

    @ManyToOne
    @JoinColumn(name = "organization_id")
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @PrePersist
    protected void onCreate() {
        setCreated(new Date());
        setUpdated(new Date());
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdated(new Date());
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
