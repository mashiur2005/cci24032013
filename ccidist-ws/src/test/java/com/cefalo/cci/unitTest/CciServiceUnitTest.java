package com.cefalo.cci.unitTest;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.utils.XpathUtils;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static junit.framework.Assert.*;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class CciServiceUnitTest {
    @Inject
    private CciService cciService;
    @Inject
    private XpathUtils xpathUtils;

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
        /*List<SyndLink> syndLinkList = cciService.getLinks(1, 5, "Polaris", "Addressa", 12);
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
        assertEquals(2, syndLinkList.size());*/
    }

    @Test
    public void getIssueAsAtomFeed() {
        helperIssueAsAtomFeed(5, 5, 10, 3, 5);
        helperIssueAsAtomFeed(12, 4, 15, 2, 4);
        helperIssueAsAtomFeed(1, 5, 0, 1, 0);
        helperIssueAsAtomFeed(5, 5, 15, 3, 5);
        helperIssueAsAtomFeed(15, 15, 30, 3, 15);
        helperIssueAsAtomFeed(1, 12, 4, 1, 4);
    }

    public void helperIssueAsAtomFeed(int start, int limit, final int numberOfFiles, int expectedLinkCount, int expectedEntryCount) {
        /*mockCciService = new CciServiceImpl(){
            @Override
            public List<String> getAllFileNamesInDirectory(String dirPath) {
                List<String> listFileNames = new ArrayList<String>();
                for (int i = 0; i < numberOfFiles; i++) {
                    listFileNames.add("demoFile" + i + ".epub");
                }
                return listFileNames;
            }
        };
        SyndFeed syndFeed = mockCciService.getIssueAsAtomFeed(mockCciService.getAllFileNamesInDirectory("/using/as/mock/fileLoading"),"/cciService/", "Polaris", "Addressa", start, limit);

        assertEquals("number of links: ", syndFeed.getLinks().size(), expectedLinkCount);
        assertEquals("number of entry: ", syndFeed.getEntries().size(), expectedEntryCount);

        List<String> actualList = new ArrayList<String>();
        for (int i = 0; i < syndFeed.getEntries().size(); i++) {
            SyndEntryImpl syndEntry = (SyndEntryImpl) syndFeed.getEntries().get(i);
            actualList.add(syndEntry.getTitle());
        }
        List<String> expectedList = new ArrayList<String>();
        if (mockCciService.getAllFileNamesInDirectory("").size() > 0) {
            int toIndex = 0;
            if (start + limit - 1 > mockCciService.getAllFileNamesInDirectory("").size()) {
                toIndex = mockCciService.getAllFileNamesInDirectory("").size();
            } else {
                toIndex = start + limit - 1;
            }
            expectedList = mockCciService.getAllFileNamesInDirectory("").subList(start -1 , toIndex);
        }
        assertEquals("element by element check: ", expectedList, actualList);*/
    }
}
