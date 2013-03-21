package com.cefalo.cci.model;

import com.cefalo.cci.utils.Utils;
import org.hibernate.annotations.*;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "issue")
@org.hibernate.annotations.Cache(region = "com.cefalo.cci.model.Issue", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Issue extends Persistent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Publication publication;
    private Platform platform;
    private EpubFile epubFile;
    private Date created;
    private Date updated;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epub_file_id")
    public EpubFile getEpubFile() {
        return epubFile;
    }

    public void setEpubFile(EpubFile epubFile) {
        this.epubFile = epubFile;
    }

    @ManyToOne
    @JoinColumn(name = "publication_id")
    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    @ManyToOne
    @JoinColumn(name = "platform_id")
    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getCreated() {
        return Utils.convertDateWithTZ(created);
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return Utils.convertDateWithTZ(updated);
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

/*
    @PrePersist
    protected void onCreate() {
        setCreated(Utils.convertDateWithTZ(new Date()));
        setUpdated(Utils.convertDateWithTZ(new Date()));
    }
*/

    @PreUpdate
    protected void onUpdate() {
        setUpdated(Utils.convertDateWithTZ(new Date()));
    }
}
