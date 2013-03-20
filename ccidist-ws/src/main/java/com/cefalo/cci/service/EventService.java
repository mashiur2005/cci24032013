package com.cefalo.cci.service;

import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Events;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.sun.syndication.feed.synd.SyndFeed;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface EventService {
    void addEvents(long fileId, Set<String> updatedSet, Set<String> insertedSet, Set<String> deletedSet);
    SyndFeed getEventQueueAtomFeed(Issue issue, Organization organization, Publication publication,
                                   long start, long limit, String deviceType, Date fromDate, String sortOrder,
                                   ResourceLocator resourceLocator);
    SyndFeed generateEventQueueAtomFeed(List<Events> eventsList, Issue issue, Organization organization, Publication publication,
                               long start, long limit, String deviceType, Date fromDate, String sortOrder,
                               long total, ResourceLocator resourceLocator);
}
