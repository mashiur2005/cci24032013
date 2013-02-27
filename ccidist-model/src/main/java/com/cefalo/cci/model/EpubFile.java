package com.cefalo.cci.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;

@Entity
@Table(name = "epub_file")
public class EpubFile extends Persistent implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private Blob file;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Lob
    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }
}
