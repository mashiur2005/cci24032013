package com.cefalo.cci.storage;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNull;

public class StorageUnitTest {
    private final CacheStorage cacheStorage;

    public StorageUnitTest() {
        cacheStorage = new CacheStorage();

    }

    @Test
    public void getFragmentTest() {
        List<String> resourceIds = Arrays.asList(null, "/Polaris", "/Polaris/Addressa");
        List<String> fragmentIds = Arrays.asList("/META-INF/container.xml", null, "/META-INF/container.xml");

        for (int i = 0; i < 3; i++) {
            try {
                InputStream in = cacheStorage.getFragment(URI.create(resourceIds.get(i)), URI.create(fragmentIds.get(i)));
                assertNull(in);
            } catch (NullPointerException | IOException ex) {

            }
        }
    }

   @Test
    public void downloadEpubFileTest() {
       try {
           InputStream in = cacheStorage.get(null);
           assertNull(in);
       } catch (NullPointerException | IOException ex) {

       }
    }

}
