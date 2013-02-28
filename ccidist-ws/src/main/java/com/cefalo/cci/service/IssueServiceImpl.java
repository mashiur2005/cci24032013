package com.cefalo.cci.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.google.inject.Inject;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;

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
            long start,
            long limit,
            ResourceLocator resourceLocator) {
        checkArgument(start > 0 && limit > 0);

        return getIssueAsAtomFeed(
                issueDao.getIssueListByPublicationId(publication.getId(), start, limit),
                organization,
                publication,
                start,
                limit,
                (int)issueDao.getIssueCountByPublicationId(publication.getId()),
                resourceLocator);
    }

    @SuppressWarnings("unchecked")
    private SyndFeed getIssueAsAtomFeed(List<Issue> issues, Organization organization, Publication publication,
            long start, long limit, long total, ResourceLocator resourceLocator) {
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

        List<SyndLink> links = getLinks(start, limit, total,
                resourceLocator.getIssueListURI(organization.getId(), publication.getId()).toString());
        feed.setLinks(links);

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        for (Issue issue : issues) {
            SyndEntry syndEntry = new SyndEntryImpl();
            syndEntry.setUri(issue.getId()); // TODO: What is this????????
            syndEntry.setUpdatedDate(issue.getUpdated());
            syndEntry.setTitle(issue.getName());
            syndEntry.setAuthor(publicationName);
            syndEntry.setLink(resourceLocator.getIssueURI(organization.getId(), publication.getId(), issue.getId())
                    .toString());
            entries.add(syndEntry);
        }
        feed.setEntries(entries);

        return feed;
    }

    private List<SyndLink> getLinks(long start, long limit, long total, String issueListUri) {
        List<SyndLink> links = new ArrayList<SyndLink>();
        links.add(createAtomLink("self", start, limit, issueListUri));

        if (start > 1) {
            // There is a prev link
            links.add(createAtomLink("prev", Math.max(1, start - limit), limit, issueListUri));
        }
        if ((start + limit) < (total + 1)) {
            // There is a next link
            links.add(createAtomLink("next", Math.min(start + limit, total), limit, issueListUri));
        }

        return links;
    }

    private SyndLink createAtomLink(String relation, long start, long limit, String baseIssueListUri) {
        SyndLink self = new SyndLinkImpl();
        self.setRel(relation);
        self.setHref(String.format("%s?start=%s&limit=%s", baseIssueListUri, start, limit));
        return self;
    }
}
