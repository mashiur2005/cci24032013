package com.cefalo.cci.storage;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CacheStorage implements Storage {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    @Named("databaseStorage")
    Storage databaseStorage;

    @Override
    public InputStream get(URI resourceID) throws IOException {
        Preconditions.checkNotNull(resourceID, "Resource Id can not be null");
        return databaseStorage.get(resourceID);
    }

    @Override
    public InputStream getFragment(URI resourceID, URI fragmentPath) throws IOException {

        Preconditions.checkNotNull(resourceID, "Resource Id can not be null");
        Preconditions.checkNotNull(fragmentPath, "Fragment Path can not be null");

        String fileName = fragmentPath.getPath();
        OutputStream out = null;
        InputStream in = databaseStorage.get(resourceID);
        ZipInputStream zis = new ZipInputStream(in);
        out = new ByteArrayOutputStream();
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            if (fileName.contains(ze.getName())) {
                ByteStreams.copy(zis, out);
                Closeables.close(out, true);
                break;
            }
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }

    @Override
    public URI create(InputStream data) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URI replace(URI resourceID, InputStream data) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(URI resourceID, InputStream modifiedData) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URI delete(URI resourceID) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
