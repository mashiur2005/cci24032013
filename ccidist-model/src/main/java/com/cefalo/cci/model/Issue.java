package com.cefalo.cci.model;

import org.hibernate.annotations.LazyToOne;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "issue")
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

    @ManyToOne
    @JoinColumn(name = "epub_file_id")
    @LazyToOne(org.hibernate.annotations.LazyToOneOption.PROXY)
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
}
