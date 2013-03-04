package com.cefalo.cci.storage;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.*;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CacheStorage implements Storage {
    @Inject
    @Named("databaseStorage")
    Storage databaseStorage;

    @Override
    public InputStream get(URI resourceID) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");

        return databaseStorage.get(resourceID);
    }

    @Override
    public InputStream getFragment(URI resourceID, URI fragmentPath) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");
        checkNotNull(fragmentPath, "Fragment Path can not be null");

        String fileName = fragmentPath.getPath();
        boolean exceptionHappened = false;
        InputStream in = null;
        ZipInputStream zipInputStream = null;
        try {
            in = databaseStorage.get(resourceID);
            zipInputStream = new ZipInputStream(in);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (fileName.equals(zipEntry.getName())) {
                    return zipInputStream;
                }
            }
        } catch (FileNotFoundException fnfe) {
            exceptionHappened = true;
            throw new FileNotFoundException();
        } catch (IOException io) {
            exceptionHappened = true;
            throw new IOException();
        } finally {
            if (exceptionHappened) {
                Closeables.close(zipInputStream, exceptionHappened);
                Closeables.close(in, exceptionHappened);
            }
        }
        throw new FileNotFoundException();
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
