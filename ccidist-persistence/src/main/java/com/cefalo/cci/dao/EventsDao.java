package com.cefalo.cci.dao;

import com.cefalo.cci.model.Events;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface EventsDao {
    List<Events> getEventsByEpubId(long epub_file_id, long start, long maxResult, String sortOrder, Date fromDate);

    long getEventsCountByEpubId(long epub_file_id, Date fromDate);

    void saveEvents(Set<Events> eventSet);
}
