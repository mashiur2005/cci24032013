package com.cefalo.cci.model;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class Persistent implements Serializable {

    @Version
    long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
