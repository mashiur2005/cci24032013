package com.cefalo.cci.service;

import com.cefalo.cci.model.Events;
import com.cefalo.cci.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class EventServiceImpl implements EventService {
    private Set<Events> events;
    @Override
    public void addEvents(long fileId,Set<String> updatedSet, Set<String> insertedSet, Set<String> deletedSet) {

        //seperate method
        if (updatedSet.isEmpty() && insertedSet.isEmpty() && deletedSet.isEmpty()) {
            return;
        }

        if (events == null || events.size() == 0) {
            events = new HashSet<>();
        }
        processFileSetAndAddEvents(fileId, Utils.FILE_UPDATED, updatedSet);
        processFileSetAndAddEvents(fileId, Utils.FILE_INSERTED, insertedSet);
        processFileSetAndAddEvents(fileId, Utils.FILE_DELETED, deletedSet);


    }

    public void processFileSetAndAddEvents(long fileId, int fileStatus,  Set<String> fileSet) {
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
