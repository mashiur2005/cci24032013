package com.cefalo.cci.storage;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.*;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    //used as temporarily. Will be removed
    private ConcurrentMap<String, String> keyStor = new ConcurrentHashMap<String, String> ();

    @Override
    public InputStream get(URI resourceID) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");

        return databaseStorage.get(resourceID);
    }

    @Override
    public InputStream getFragment(URI resourceID, URI fragmentPath) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");
        checkNotNull(fragmentPath, "Fragment Path can not be null");

        String fileId = resourceID.getPath();

        //used as temporarily. Will be removed....
        keyStor.putIfAbsent(fileId, fileId);

        String fileName = fragmentPath.getPath();
        String fileDirPath =  cacheDirFullPath + fileSystemSeperator + fileId;
        File resourceFile = new File(fileDirPath);

        //Same fileId  value might have different reference. temporary soluation
        synchronized (keyStor.get(fileId)) {
            if (!resourceFile.exists()) {
                extractAndStoreEpub(resourceID);
            }
            String fileLocationPath = fileDirPath + fileSystemSeperator + fileName;

            if (new File(fileLocationPath).exists()) {
                return new FileInputStream(fileLocationPath);
            }
            throw new FileNotFoundException();
        }
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

    public void extractAndStoreEpub(URI resourceId) throws IOException {
        checkNotNull(resourceId, "Resource Id can not be null");

        InputStream inputStream = null;
        ZipInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        String fileName = resourceId.getPath();

        try {
            inputStream = databaseStorage.get(resourceId);
            zipInputStream = new ZipInputStream(inputStream);
            ZipEntry entry;
            String name;
            File file = new File(cacheDirFullPath + fileSystemSeperator + fileName);
            if (!file.exists()) {
                Files.createParentDirs(file);
            }
            while ((entry = zipInputStream.getNextEntry()) != null) {
                name = entry.getName();
                file = new File(cacheDirFullPath + fileSystemSeperator + fileName+ fileSystemSeperator + name);
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
        } catch (FileNotFoundException fn) {
            throw new FileNotFoundException();
        } catch (IOException io) {
            throw new IOException();
        } finally {
            //need to discuss with partha bhai
            Closeables.close(inputStream, false);
            Closeables.close(zipInputStream, false);
            Closeables.close(fileOutputStream, false);
        }
    }

    public InputStream getFragmentFromCache(String filePath) throws IOException {
        FileInputStream fileInputStream = null;
        boolean exceptionhappened = false;

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
