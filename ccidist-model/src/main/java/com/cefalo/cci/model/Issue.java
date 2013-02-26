package com.cefalo.cci.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;

@Entity
@Table(name = "issue")
public class Issue extends Persistent implements Serializable {
    private long id;
    private String name;
    private Publication publication;
    private Platform platform;
    private Blob epubFile;

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

    @Lob
    public Blob getEpubFile() {
        return epubFile;
    }

    public void setEpubFile(Blob epubFile) {
        this.epubFile = epubFile;
    }
}
