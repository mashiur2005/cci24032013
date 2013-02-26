package com.cefalo.cci.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pubt_platform")
public class PubtPlatform extends Persistent implements Serializable {
    private long id;
    private Publication publication;
    private Platform platform;


    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
