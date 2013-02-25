package com.cefalo.cci.unitTest;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.service.CciServiceImpl;
import com.cefalo.cci.utils.XpathUtils;
import com.google.inject.Inject;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class CciServiceUnitTest {
    @Inject
    private CciService cciService;
    @Inject
    private XpathUtils xpathUtils;

    private CciService mockCciService;


    @Test
    public void getAllFileNamesInDirectoryTest() {
        File file = FileUtils.getFile("src", "test", "resources", "epubs");
        String fileDir = file.getAbsolutePath();
        List<String> listFileNames = cciService.getAllFileNamesInDirectory(fileDir);
        assertEquals(2, listFileNames.size());
        assertTrue(listFileNames.contains("widget-quiz-20121022.epub"));

        listFileNames = cciService.getAllFileNamesInDirectory("");
        assertEquals(0, listFileNames.size());
        assertTrue(listFileNames.isEmpty());

        listFileNames = cciService.getAllFileNamesInDirectory(null);
        assertEquals(0, listFileNames.size());
        assertTrue(listFileNames.isEmpty());
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

    @Test
    public void getIssueAsAtomFeed() {
        helperIssueAsAtomFeed(5, 5, 10, 3, 5);
        helperIssueAsAtomFeed(12, 4, 15, 2, 4);
        helperIssueAsAtomFeed(1, 5, 0, 0, 0);
    }

    public void helperIssueAsAtomFeed(int start, int limit, final int numberOfFiles, int expectedLinkCount, int expectedEntryCount) {
        mockCciService = new CciServiceImpl(){
            @Override
            public List<String> getAllFileNamesInDirectory(String dirPath) {
                List<String> listFileNames = new ArrayList<String>();
                for (int i = 0; i < numberOfFiles; i++) {
                    listFileNames.add("demoFile" + i + ".epub");
                }
                return listFileNames;
            }
        };
        SyndFeed syndFeed = mockCciService.getIssueAsAtomFeed("Polaris", "Addressa", "/home/mashiur/epubs", start, limit);

        assertEquals("number of links", syndFeed.getLinks().size(), expectedLinkCount);
        assertEquals("number of entry:", syndFeed.getEntries().size(), expectedEntryCount);

        List<String> actualList = new ArrayList<String>();
        for (int i = 0; i < syndFeed.getEntries().size(); i++) {
            SyndEntryImpl syndEntry = (SyndEntryImpl) syndFeed.getEntries().get(i);
            actualList.add(syndEntry.getTitle());
        }
        List<String> expectedList = new ArrayList<String>();
        if (mockCciService.getAllFileNamesInDirectory("").size() > 0) {
            expectedList = mockCciService.getAllFileNamesInDirectory("").subList(start -1 , start + limit - 1);
        }
        assertEquals("element by element check", expectedList, actualList);
    }
}
