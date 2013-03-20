package com.cefalo.cci.dao;

import com.cefalo.cci.model.Events;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EventsDaoImpl implements EventsDao{

    @Inject
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Events> getEventsByEpubId(long epub_file_id, long start, long maxResult, String sortOrder, Date fromDate) {
        return entityManager.createQuery("select e from Events e where e.epubFileId =:Id and e.created >=:fromDate order by e.category " + sortOrder)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", "query.eventQueueList")
                .setParameter("Id", epub_file_id)
                .setParameter("fromDate", fromDate)
                .setFirstResult((int) start)
                .setMaxResults((int) maxResult)
                .getResultList();
    }

    @Override
    public long getEventsCountByEpubId(long epub_file_id, Date fromDate) {
        return (Long) entityManager.createQuery("select count(e) from Events e where e.epubFileId =:Id and e.created >=:fromDate")
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", "query.eventQueueList")
                .setParameter("Id", epub_file_id)
                .setParameter("fromDate", fromDate)
                .getSingleResult();
    }

    @Override
    public void saveEvents(Set<Events> eventSet) {
        for (Events event : eventSet) {
            entityManager.persist(event);
        }
    }
}
