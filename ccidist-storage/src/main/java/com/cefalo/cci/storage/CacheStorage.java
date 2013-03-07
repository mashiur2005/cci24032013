package com.cefalo.cci.storage;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.*;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class CacheStorage implements Storage {
    @Inject
    @Named("databaseStorage")
    Storage databaseStorage;

    @Inject
    @Named("cacheDirFullPath")
    private String cacheDirFullPath;

    @Inject
    @Named("fileSystemSeperator")
    private String fileSystemSeperator;

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

    @Override
    public void fetchAndWriteEpub(URI resourceId, String organizationId, String publicationId) throws IOException {
        checkNotNull(resourceId, "Resource Id can not be null");
        checkNotNull(organizationId, "Organization Id can not be null");
        checkNotNull(publicationId, "Publication Id can not be null");

        InputStream inputStream = null;
        ZipInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;
        boolean exceptionHappened = false;

        try {
            inputStream = databaseStorage.get(resourceId);
            zipInputStream = new ZipInputStream(inputStream);
            ZipEntry entry;
            String name;
            file = new File(cacheDirFullPath + fileSystemSeperator + organizationId + fileSystemSeperator + publicationId + fileSystemSeperator + resourceId.getPath() + fileSystemSeperator);
            if (!file.exists()) {
                Files.createParentDirs(file);
            }
            while ((entry = zipInputStream.getNextEntry()) != null) {
                name = entry.getName();
                file = new File(cacheDirFullPath + fileSystemSeperator + organizationId + fileSystemSeperator + publicationId + fileSystemSeperator + resourceId.getPath() + fileSystemSeperator + name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                fileOutputStream = new FileOutputStream(file);
                ByteStreams.copy(zipInputStream, fileOutputStream);
                fileOutputStream.close();
            }
            inputStream.close();
            zipInputStream.close();
        } catch (FileNotFoundException fn) {
            exceptionHappened = true;
            fn.printStackTrace();
            throw new FileNotFoundException();
        } catch (IOException io) {
            exceptionHappened = true;
            io.printStackTrace();
            throw new IOException();
        } finally {
            if (exceptionHappened) {
                Closeables.close(inputStream, exceptionHappened);
                Closeables.close(zipInputStream, exceptionHappened);
                Closeables.close(fileOutputStream, exceptionHappened);
            }
        }
    }

    @Override
    public InputStream getFragmentFromCache(URI resourceId, URI fragmentPath, String filePath) throws IOException {
        checkNotNull(resourceId, "Resource Id can not be null");
        checkNotNull(fragmentPath, "fragmentPath can not be null");

        FileInputStream fileInputStream = null;
        boolean exceptionhappened = false;

        if (!new File(filePath).exists()) {
             throw new FileNotFoundException();
        }

        try {
            fileInputStream = new FileInputStream(filePath);
            return fileInputStream;
        } catch (FileNotFoundException fn) {
            exceptionhappened = true;
            throw new FileNotFoundException();
        } finally {
            if (exceptionhappened) {
                Closeables.close(fileInputStream, exceptionhappened);
            }
        }
    }
}
