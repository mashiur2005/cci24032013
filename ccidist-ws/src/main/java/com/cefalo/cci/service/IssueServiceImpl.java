package com.cefalo.cci.service;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.google.inject.Inject;
import com.sun.syndication.feed.synd.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IssueServiceImpl implements IssueService {
    @Inject
    private IssueDao issueDao;

    public Issue getIssue(String issueId) {
        // TODO: Maybe throw an exception from here if issue not found????
        return issueDao.getIssue(issueId);
    }

    @Override
    public List<Issue> getIssueListByPublicationId(String publicationId) {
        return issueDao.getIssueListByPublicationId(publicationId);
    }

    @Override
    public SyndFeed getIssuesAsAtomFeed(
            Organization organization,
            Publication publication,
            int start,
            int limit,
            ResourceLocator resourceLocator) {

        return getIssueAsAtomFeed(
                getIssueListByPublicationId(publication.getId()),
                organization,
                publication,
                start,
                limit,
                resourceLocator);
    }

    @SuppressWarnings("unchecked")
    private SyndFeed getIssueAsAtomFeed(List<Issue> issues, Organization organization, Publication publication, int start,
            int limit, ResourceLocator resourceLocator) {
        String publicationName = publication.getName();
        String organizationName = organization.getName();

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_1.0");
        feed.setTitle(String.format("%s issues", publicationName));
        feed.setUri(resourceLocator.getIssueListURI(organizationName, publicationName).toString());
        feed.setPublishedDate(new Date());

        SyndPerson syndPerson = new SyndPersonImpl();
        syndPerson.setName(publicationName);
        feed.getAuthors().add(syndPerson);

        List<SyndLink> links = getLinks(start, limit, organizationName, publicationName, issues.size());

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        if (!links.isEmpty()) {
            SyndEntry syndEntry;
            int toIndex = 0;

            if (start + limit - 1 > issues.size()) {
                toIndex = issues.size();

            } else {
                toIndex = start + limit - 1;
            }
            for (Issue issue : issues.subList(start - 1, toIndex)) {
                syndEntry = new SyndEntryImpl();
                syndEntry.setUri("entry Id test"); // ??????
                syndEntry.setUpdatedDate(issue.getUpdated());
                syndEntry.setTitle(issue.getName());
                syndEntry.setAuthor(publicationName);
                syndEntry.setLink(resourceLocator.getIssueURI(organization.getId(), publication.getId(), issue.getId())
                        .toString());
                entries.add(syndEntry);
            }

        } else {
            SyndLink self = new SyndLinkImpl();
            self.setRel("self");
            self.setHref(resourceLocator.getIssueListURI(organization.getId(), publication.getId()).toString()
                    .concat("?start=0&limit=0"));
            links.add(self);
        }

        feed.setLinks(links);
        feed.setEntries(entries);

        return feed;
    }

    private List<SyndLink> getLinks(int start, int limit, String organizationName, String publicationName, int totalFile) {
        List<SyndLink> links = new ArrayList<SyndLink>();

        if (start <= 0 || limit <= 0 || start > totalFile) {
            return links;
        }

        if (start > 0 && limit > 0 && start + limit - 1 > totalFile) {
            SyndLink self = new SyndLinkImpl();
            self.setRel("self");
            self.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + totalFile + "&start=" + start);
            links.add(self);
            return links;
        }

        int prevStart = 0;
        int prevLimit = 0;
        int selfStart = 0;
        int selfLimit = 0;
        int nextStart = 0;
        int nextLimit = 0;
        boolean addPrev = true;
        boolean addNext = true;

        if (start + limit - 1 == totalFile) {
            addNext = false;
        }

        if (start > limit) {
            prevStart = start - limit;
            selfStart = start;
            nextStart = start + limit;

            prevLimit = limit;
            selfLimit = limit;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        } else if (start < limit && start > 1) {
            prevStart = 1;
            selfStart = start;
            nextStart = start + limit;

            prevLimit = start - prevStart;
            selfLimit = limit;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        } else if (start == 1) {
            addPrev = false;
            selfStart = start;
            nextStart = start + limit;

            selfLimit = limit;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        } else if (start == limit) {
            prevStart = start - limit + 1;
            selfStart = start;
            nextStart = start + limit;

            selfLimit = limit;
            prevLimit = start - prevStart;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        }

        if (addPrev) {
            SyndLink prev = new SyndLinkImpl();
            prev.setRel("prev");
            prev.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + prevLimit + "&start=" + prevStart);
            links.add(prev);
        }

        SyndLink self = new SyndLinkImpl();
        self.setRel("self");
        self.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + selfLimit + "&start=" + selfStart);
        links.add(self);
        if (addNext) {
            SyndLink next = new SyndLinkImpl();
            next.setRel("next");
            next.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + nextLimit + "&start=" + nextStart);
            links.add(next);
        }

        return links;
    }
}
