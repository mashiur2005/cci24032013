package com.cefalo.cci.model;

import com.cefalo.cci.utils.Utils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "events")
public class Events extends Persistent implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long epubFileId;
    private String path;
    private String category;
    private Date created;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "epub_file_id")
    public long getEpubFileId() {
        return epubFileId;
    }

    public void setEpubFileId(long epubFileId) {
        this.epubFileId = epubFileId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getCreated() {
        return Utils.convertDateWithTZ(created);
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @PrePersist
    protected void onCreate() {
        setCreated(Utils.convertDateWithTZ(new Date()));
    }
}
