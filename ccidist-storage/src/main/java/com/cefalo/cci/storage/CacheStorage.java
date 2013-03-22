package com.cefalo.cci.storage;

import com.cefalo.cci.utils.Utils;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class CacheStorage implements Storage {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Utils utils = new Utils();

    @Inject
    @Named("databaseStorage")
    Storage databaseStorage;

    @Inject
    @Named("cacheDirFullPath")
    private String cacheDirFullPath;

    @Inject
    @Named("cacheKeyStore")
    private final ConcurrentMap<String, String> fileKeyStor = new ConcurrentHashMap<String, String> ();

    @Inject
    @Named("cacheEpubDirFullPath")
    private String cacheEpubDirFullPath;

    @Override
    public InputStream get(URI resourceID) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");

        File epubFile = new File(cacheEpubDirFullPath + resourceID.getPath());
        if (epubFile.exists()) {
            logger.info(String.format("File Download served from file system %s ", epubFile.getAbsolutePath()));
            return utils.readFileFromDir(epubFile.getAbsolutePath());
        } else {
            logger.info(String.format("File Download served from database and resource id is %s ", resourceID.getPath()));
            boolean exceptionHappened = false;
            InputStream data = null;
            try{
                data = databaseStorage.get(resourceID);
                utils.writeZipFileToDir(data, cacheEpubDirFullPath + resourceID.getPath());
                data.close();
                data = utils.readFileFromDir(cacheEpubDirFullPath + resourceID.getPath());
                return data;
            } catch (IOException io) {
                exceptionHappened = true;
                throw io;
            } finally {
                if (exceptionHappened) {
                    Closeables.close(data, false);
                }
            }
        }
    }

    @Override
    public InputStream getFragment(URI resourceID, URI fragmentPath) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");
        checkNotNull(fragmentPath, "Fragment Path can not be null");

        String fileId = resourceID.getPath();

        //used as temporarily. Will be removed....
        fileKeyStor.putIfAbsent(fileId, fileId);

        String fileName = fragmentPath.getPath();
        String fileDirPath =  cacheDirFullPath + "/" + fileId;
        File resourceFile = new File(fileDirPath);

        //Same fileId  value might have different reference. temporary soluation
        synchronized (fileKeyStor.get(fileId)) {
            if (!resourceFile.exists()) {
                extractAndStoreEpub(resourceID);
            }
            String fileLocationPath = fileDirPath + "/" + fileName;

            if (new File(fileLocationPath).exists()) {
                return new FileInputStream(fileLocationPath);
            }
            throw new FileNotFoundException();
        }
    }

    @Override
    public URI create(InputStream data) throws IOException {
        long currentTime = System.currentTimeMillis();
        File oldFile = null;
        try{
            utils.writeZipFileToDir(data, cacheEpubDirFullPath + currentTime);
            data.close();
            data = utils.readFileFromDir(cacheEpubDirFullPath + currentTime);
            URI epubResource = databaseStorage.create(data);
            data.close();
            oldFile = new File(cacheEpubDirFullPath + currentTime);
            File newFile = new File(cacheEpubDirFullPath + epubResource.getPath());
            Files.copy(oldFile, newFile);
            return epubResource;
        } catch (IOException io) {
            throw io;
        } finally {
            Closeables.close(data, false);
            if (oldFile != null) {
                logger.info(String.format("files deleted : %s", oldFile.delete()));
            }
        }
    }

    @Override
    public URI replace(URI resourceID, InputStream data) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(URI resourceID, InputStream modifiedData) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");
        try{
            logger.info(String.format("%s rewritting for update", cacheEpubDirFullPath + resourceID.getPath()));
            utils.writeZipFileToDir(modifiedData, cacheEpubDirFullPath + resourceID.getPath());
            modifiedData.close();
            modifiedData = utils.readFileFromDir(cacheEpubDirFullPath + resourceID.getPath());
            databaseStorage.update(resourceID, modifiedData);
            modifiedData.close();
        } catch (IOException io) {
            throw io;
        } finally {
            Closeables.close(modifiedData, false);
        }
    }

    @Override
    public URI delete(URI resourceID) throws IOException {
        checkNotNull(resourceID, "Resource Id can not be null");

        File extractedFile = new File(cacheDirFullPath + resourceID.getPath());
        File epubFile = new File(cacheEpubDirFullPath + resourceID.getPath());
        utils.deleteRecursive(extractedFile);
        utils.deleteRecursive(epubFile);
        return resourceID;
    }

    @Override
    public void invalidateExtractedFileCache(String key) {
        logger.info(String.format("Invalidating at : %s and the file id is : %s", System.currentTimeMillis(), key));
        fileKeyStor.remove(key);
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
            File file = new File(cacheDirFullPath + "/" + fileName);
            if (!file.exists()) {
                Files.createParentDirs(file);
            }
            while ((entry = zipInputStream.getNextEntry()) != null) {
                name = entry.getName();
                file = new File(cacheDirFullPath + "/" + fileName+ "/" + name);
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
