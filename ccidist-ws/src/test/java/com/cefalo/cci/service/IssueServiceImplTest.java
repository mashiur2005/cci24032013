package com.cefalo.cci.service;

import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.unitTest.GuiceJUnitRunner;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({InjectModule.class })
public class IssueServiceImplTest {

    private IssueServiceImpl issueServiceImpl;

    @Before
    public void beforeTest() {
        issueServiceImpl = new IssueServiceImpl();
    }

    @Test
    public void getLinksTest() {
        List<String> expectedAllRelList = new ArrayList<String>();
        expectedAllRelList.add("self");
        expectedAllRelList.add("prev");
        expectedAllRelList.add("next");

        List<String> selfPrevList = new ArrayList<String>();
        selfPrevList.add("self");
        selfPrevList.add("prev");

        List<String> selfNextList = new ArrayList<String>();
        selfNextList.add("self");
        selfNextList.add("next");

        String issueListUri = "/cciService/polaris/addressa/issue";
        List<SyndLink> syndLinkList = issueServiceImpl.getLinks(1, 5, 12, issueListUri);
        assertEquals(2, syndLinkList.size());
        assertEquals("self: start=1&limit=5", issueListUri + "?start=1&limit=5", syndLinkList.get(0).getHref());
        assertEquals("next: start=6&limit=5", issueListUri + "?start=6&limit=5", syndLinkList.get(1).getHref());

        List<String> actualList = new ArrayList<String>();
        for (SyndLink aSyndLinkList : syndLinkList) {
            actualList.add(aSyndLinkList.getRel());
        }
        assertEquals("relation by relation check: self next", selfNextList, actualList);

        syndLinkList = issueServiceImpl.getLinks(2, 5, 12, issueListUri);
        assertEquals(3, syndLinkList.size());
        assertEquals("self: start=2&limit=5",issueListUri + "?start=2&limit=5", syndLinkList.get(0).getHref());
        assertEquals("prev: start=1&limit=5", issueListUri + "?start=1&limit=5", syndLinkList.get(1).getHref());
        assertEquals("next: start=7&limit=5", issueListUri + "?start=7&limit=5", syndLinkList.get(2).getHref());

        actualList = new ArrayList<String>();
        for (SyndLink aSyndLinkList : syndLinkList) {
            actualList.add(aSyndLinkList.getRel());
        }

        assertEquals("relation by relation check: self prev next", expectedAllRelList, actualList);

        syndLinkList = issueServiceImpl.getLinks(7, 5, 12, issueListUri);
        assertEquals(3, syndLinkList.size());
        assertEquals("self: start=7&limit=5",issueListUri + "?start=7&limit=5", syndLinkList.get(0).getHref());
        assertEquals("prev: start=2&limit=5", issueListUri + "?start=2&limit=5", syndLinkList.get(1).getHref());
        assertEquals("next: start=12&limit=5", issueListUri + "?start=12&limit=5", syndLinkList.get(2).getHref());

        actualList = new ArrayList<String>();

        for (SyndLink aSyndLinkList : syndLinkList) {
            actualList.add(aSyndLinkList.getRel());
        }

        assertEquals("relation by relation check: self prev next", expectedAllRelList, actualList);

        syndLinkList = issueServiceImpl.getLinks(7, 6, 12, issueListUri);
        assertEquals(2, syndLinkList.size());
        assertEquals("self: start=7&limit=6",issueListUri + "?start=7&limit=6", syndLinkList.get(0).getHref());
        assertEquals("prev: start=1&limit=6", issueListUri + "?start=1&limit=6", syndLinkList.get(1).getHref());

        actualList = new ArrayList<String>();

        for (SyndLink aSyndLinkList : syndLinkList) {
            actualList.add(aSyndLinkList.getRel());
        }

        assertEquals("relation by relation check: self prev", selfPrevList, actualList);
    }

    @Test
    public void getIssueAsAtomFeedTest() {
        testAtomFeedHelper(5, 5, 10, 3, 5);
        testAtomFeedHelper(12, 4, 15, 2, 4);
        testAtomFeedHelper(1, 5, 0, 1, 0);
        testAtomFeedHelper(5, 5, 15, 3, 5);
        testAtomFeedHelper(15, 15, 30, 3, 15);
        testAtomFeedHelper(1, 12, 4, 1, 4);
    }

    public void testAtomFeedHelper(int start, int limit, int numberOfIssues, int expectedLinkCount, int expectedEntryCount) {
        String organizationId = "polaris";
        String publicationId = "addressa";

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName(organizationId);

        Publication publication = new Publication();
        publication.setId(publicationId);
        publication.setName(publicationId);

        List<Issue> dummyIssueList = getDummyList(numberOfIssues);

        ResourceLocator mockResourceLocator = mock(ResourceLocator.class);

        for (int i = 0; i < numberOfIssues; i++) {
            when(mockResourceLocator.getIssueURI(organization.getId(), publication.getId(), dummyIssueList.get(i).getId())).thenReturn(URI.create("/" + organizationId + "/" + publicationId + "/" + dummyIssueList.get(i).getId()));
        }

        when(mockResourceLocator.getIssueListURI(organizationId, publicationId)).thenReturn(URI.create("/" + organizationId + "/" + publicationId));

        int toIndex = 0;
        if (start + limit - 1 > numberOfIssues) {
            toIndex = numberOfIssues;
        } else {
            toIndex = start + limit - 1;
        }

        SyndFeed syndFeed = issueServiceImpl.getIssueAsAtomFeed(dummyIssueList.subList(start - 1, toIndex), organization, publication, start,limit, numberOfIssues, mockResourceLocator);

        Assert.assertEquals("number of links: ", syndFeed.getLinks().size(), expectedLinkCount);
        Assert.assertEquals("number of entry: ", syndFeed.getEntries().size(), expectedEntryCount);

        List<String> actualList = new ArrayList<String>();

        for (int i = 0; i < syndFeed.getEntries().size(); i++) {
            SyndEntryImpl syndEntry = (SyndEntryImpl) syndFeed.getEntries().get(i);
            actualList.add(syndEntry.getTitle());
        }

        List<Issue> expectedIssueList = dummyIssueList.subList(start - 1, toIndex);

        List<String> expectedNamesInList = new ArrayList<String>();

        for (int i = 0; i < expectedIssueList.size(); i++) {
            expectedNamesInList.add(expectedIssueList.get(i).getName());
        }

        Assert.assertEquals("element by element check: ", expectedNamesInList, actualList);
    }



    public List<Issue> getDummyList(int numberOfIssues) {
        List<Issue> dummyList = new ArrayList<Issue>();
        Issue dummyIssue;
        for(int i = 0; i < numberOfIssues; i++) {
            dummyIssue = new Issue();
            dummyIssue.setId(String.valueOf(i));
            dummyIssue.setName("demoIssue_" + i);
            dummyIssue.setCreated(new Date(2000 + i * 10));
            dummyIssue.setUpdated(new Date(2000 + i * 10 + 1));
            dummyList.add(dummyIssue);
        }
        return dummyList;
    }
}
