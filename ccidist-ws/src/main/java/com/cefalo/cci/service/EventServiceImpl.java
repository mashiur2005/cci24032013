package com.cefalo.cci.service;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Events;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.utils.Category;
import com.google.inject.Inject;
import com.sun.syndication.feed.synd.*;

import java.util.*;

public class EventServiceImpl implements EventService {
    private Set<Events> events;

    @Inject
    private IssueDao issueDao;

    @Inject
    private IssueService issueService;

    @Override
    public void addEvents(long fileId,Set<String> updatedSet, Set<String> insertedSet, Set<String> deletedSet) {

        if (updatedSet.isEmpty() && insertedSet.isEmpty() && deletedSet.isEmpty()) {
            return;
        }
        if (events == null || events.size() == 0) {
            events = new HashSet<>();
        }
        processFileSetAndAddEvents(fileId, Category.UPDATED.getValue(), updatedSet);
        processFileSetAndAddEvents(fileId, Category.INSERTED.getValue(), insertedSet);
        processFileSetAndAddEvents(fileId, Category.DELETED.getValue(), deletedSet);

        issueDao.saveEvents(events);
    }


    @Override
    public SyndFeed getEventQueueAtomFeed(Issue issue, Organization organization, Publication publication, long start,
                                          long limit, String deviceType, Date fromDate, String sortOrder, ResourceLocator resourceLocator) {
        return generateEventQueueAtomFeed(
                issueService.getEventsByEpubId(issue.getEpubFile().getId(), start, limit, sortOrder, fromDate),
                issue,
                organization,
                publication,
                start,
                limit,
                deviceType,
                fromDate,
                sortOrder,
                issueService.getEventsCountByEpubId(issue.getEpubFile().getId(),fromDate), resourceLocator);
    }


    @Override
    @SuppressWarnings("unchecked")
    public SyndFeed generateEventQueueAtomFeed(List<Events> eventsList, Issue issue, Organization organization, Publication publication,
                                                long start, long limit, String deviceType, Date fromDate, String sortOrder,
                                                long total, ResourceLocator resourceLocator) {
        String publicationName = publication.getName();

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_1.0");
        feed.setTitle(String.format("%s %s Updates", publicationName, issue.getName()));
        feed.setPublishedDate(issue.getUpdated());

        SyndPerson syndPerson = new SyndPersonImpl();
        syndPerson.setName(publicationName);
        feed.getAuthors().add(syndPerson);
        feed.setUri("urn:uuid:".concat(String.valueOf(issue.getEpubFile().getId())));

        List<SyndLink> links = issueService.getLinks(start, limit, deviceType, fromDate, sortOrder, total,
                resourceLocator.getEventQueueURI(organization.getId(), publication.getId(), issue.getId()).toString());
        feed.setLinks(links);

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndCategory syndCategory;

        for (Events events : eventsList) {
            SyndEntryImpl entry = new SyndEntryImpl();
            entry.setUri("urn:".concat(String.valueOf(events.getId())));
            entry.setTitle(events.getPath());
            entry.setAuthor(publicationName);
            entry.setLink(events.getPath());
            entry.setUpdatedDate(events.getCreated());

            syndCategory = new SyndCategoryImpl();
            syndCategory.setName(getCategory(events.getCategory()));
            entry.getCategories().add(syndCategory);
            entries.add(entry);
        }

        feed.setEntries(entries);
        return feed;
    }

    private String getCategory(int value) {
        if (Category.INSERTED.getValue() == value) {
            return Category.INSERTED.toString();
        } else if (Category.UPDATED.getValue() == value) {
            return Category.UPDATED.toString();
        } else if (Category.DELETED.getValue() == value) {
            return Category.DELETED.toString();
        }
        return null;
    }

    private void processFileSetAndAddEvents(long fileId, int fileStatus,  Set<String> fileSet) {
        if (!fileSet.isEmpty()) {
            for (String filePath : fileSet) {
                Events event = new Events();
                event.setEpubFileId(fileId);
                event.setCategory(fileStatus);
                event.setPath(filePath);
                events.add(event);
            }
        }
    }
}
