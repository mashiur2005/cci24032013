package com.cefalo.cci.storage;

import com.cefalo.cci.storage.FileSystemStorage;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

public class StorageUnitTest {
    private FileSystemStorage fileSystemStorage;

    public StorageUnitTest() {
        fileSystemStorage = new FileSystemStorage();

    }

    @Test
    public void getFragmentTest() {
        List<String> resourceIds = Arrays.asList(null, "/Polaris", "/Polaris/Addressa");
        List<String> fragmentIds = Arrays.asList("/META-INF/container.xml", null, "/META-INF/container.xml");

        for (int i = 0; i < 3; i++) {
            try {
                InputStream in = fileSystemStorage.getFragment(URI.create(resourceIds.get(i)), URI.create(fragmentIds.get(i)));
                assertNull(in);
            } catch (NullPointerException ex) {

            }
        }
    }

}
