package com.cefalo.cci.storage;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.model.Issue;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;

public class DatabaseStorage implements Storage {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private IssueDao issueDao;

    @Override
    public InputStream get(URI resourceID) throws IOException {
        Preconditions.checkNotNull(resourceID);

        if (logger.isInfoEnabled()) {
            logger.info("path...." + resourceID.getPath());
        }
        Issue issue = issueDao.getIssue(resourceID.getPath());
        if (issue.getEpubFile() == null || issue.getEpubFile().getFile() == null) {
            throw new FileNotFoundException("No binary file for: ");
        }

        Blob blob = issue.getEpubFile().getFile();
        try {
            return blob.getBinaryStream();
        } catch (SQLException e) {
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
}