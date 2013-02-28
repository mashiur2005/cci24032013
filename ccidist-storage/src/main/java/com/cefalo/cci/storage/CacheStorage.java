package com.cefalo.cci.storage;

import com.google.common.base.Preconditions;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class CacheStorage implements Storage {
    @Override
    public InputStream get(URI resourceID) throws IOException {
        Preconditions.checkNotNull(resourceID, "Resource Id can not be null");
        File file = new File(resourceID);
        try {
            InputStream in = new FileInputStream(file);
            Preconditions.checkNotNull(in);
            return  in;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public InputStream getFragment(URI resourceID, URI fragmentPath) {

        Preconditions.checkNotNull(resourceID, "Resource Id can not be null");
        Preconditions.checkNotNull(fragmentPath, "Fragment Path can not be null");
        final Map<String, String> env = new HashMap<String, String>();
        env.put("create", "false");
        try (FileSystem fs = FileSystems.newFileSystem(resourceID, env)) {
            Path pathInEpubfile = fs.getPath(fragmentPath.getPath());
            return new ByteArrayInputStream(Files.readAllBytes(pathInEpubfile));
        } catch (IOException e) {
            return null;
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
}
