package com.cefalo.cci.unitTest;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.cefalo.cci.service.CciService;
import com.google.inject.Inject;
import com.sun.syndication.feed.synd.SyndLink;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class CciServiceUnitTest {
    @Inject
    private CciService cciService;

    @Test
    public void getAllFileNamesInDirectoryTest() {
    	checkInvalidDirectoryPath(null, "", "   ");
    	
        Path directoryPath = Paths.get("src", "test", "resources", "epubs");
        List<String> listFileNames = cciService.getAllFileNamesInDirectory(directoryPath.toAbsolutePath().toString());
        assertEquals(2, listFileNames.size());
        assertTrue(listFileNames.contains("widget-quiz-20121022.epub"));
    }
    
    private void checkInvalidDirectoryPath(final String... paths) {
    	for (String path : paths) {
    		try {
    			cciService.getAllFileNamesInDirectory(path);
    			fail("Empty directory should be an invalid parameter");
    		}
    		catch (IllegalArgumentException ex) {
    			// This is expected
    		}
		}
    }

    @Test
    public void getLinksUnitTest() {
        List<SyndLink> syndLinkList = cciService.getLinks(1, 5, "Polaris", "Addressa", 12);
        assertEquals(2, syndLinkList.size());

        syndLinkList = cciService.getLinks(2, 5, "Polaris", "Addressa", 12);
        assertEquals(3, syndLinkList.size());

        syndLinkList = cciService.getLinks(7, 5, "Polaris", "Addressa", 12);
        assertEquals(3, syndLinkList.size());

        syndLinkList = cciService.getLinks(-1, 5, "Polaris", "Addressa", 12);
        assertEquals(0, syndLinkList.size());

        syndLinkList = cciService.getLinks(1, -5, "Polaris", "Addressa", 12);
        assertEquals(0, syndLinkList.size());

        syndLinkList = cciService.getLinks(7, 6, "Polaris", "Addressa", 12);
        assertEquals(2, syndLinkList.size());
    }
}
