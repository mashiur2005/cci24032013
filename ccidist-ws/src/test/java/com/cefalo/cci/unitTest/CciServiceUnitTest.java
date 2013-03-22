package com.cefalo.cci.unitTest;

import com.cefalo.cci.utils.XpathUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class CciServiceUnitTest {

    @Test
    public void getAllFileNamesInDirectoryTest() {
    	checkInvalidDirectoryPath(null, "", "   ");

        Path directoryPath = Paths.get("src", "test", "resources", "epubs");
        List<String> listFileNames = getAllFileNamesInDirectory(directoryPath.toAbsolutePath().toString());
        assertEquals(3, listFileNames.size());
        assertTrue(listFileNames.contains("widget-quiz-20121022.epub"));
    }

    private void checkInvalidDirectoryPath(final String... paths) {
    	for (String path : paths) {
    		try {
    			getAllFileNamesInDirectory(path);
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



    public List<String> getAllFileNamesInDirectory(final String directory) {
        String dir = Strings.nullToEmpty(directory);
        Preconditions.checkArgument(dir.trim().length() > 0, "Directory path may not be empty or null.");

        final List<String> epubFileNames = new ArrayList<String>();
        try {
            final Path directoryPath = Paths.get(directory);
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    epubFileNames.add(file.getFileName().toString());
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(directoryPath)) {
                        return super.preVisitDirectory(dir, attrs);
                    }

                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return epubFileNames;
    }
}
