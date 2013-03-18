package com.cefalo.cci.storage;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.model.EpubFile;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;

@Singleton
public class DatabaseStorage implements Storage {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private IssueDao issueDao;

    @Override
    public InputStream get(URI resourceID) throws IOException {
        Preconditions.checkNotNull(resourceID);
        long fileId = Long.valueOf(resourceID.getPath());
        if (logger.isInfoEnabled()) {
            logger.info("path...." + resourceID.getPath());
        }
        EpubFile epubFile = issueDao.getEpubFile(fileId);
        if (epubFile == null || epubFile.getFile() == null) {
            throw new FileNotFoundException(String.format("No binary file for: %s", resourceID));
        }

        byte[] fileContent = epubFile.getFile();
        try {
            return new ByteArrayInputStream(fileContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getFragment(URI resourceID, URI fragmentPath) throws IOException {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public URI delete(URI resourceID) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void invalidateExtractedFileCache(String key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
