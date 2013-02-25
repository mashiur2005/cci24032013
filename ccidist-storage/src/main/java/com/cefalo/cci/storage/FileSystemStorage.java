package com.cefalo.cci.storage;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class FileSystemStorage implements Storage {
    @Override
    public InputStream get(URI resourceID) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getFragment(URI resourceID, URI fragmentPath) {
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
