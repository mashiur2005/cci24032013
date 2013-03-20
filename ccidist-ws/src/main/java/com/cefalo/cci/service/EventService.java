package com.cefalo.cci.service;

import java.util.Set;

public interface EventService {
    void addEvents(long fileId, Set<String> updatedSet, Set<String> insertedSet, Set<String> deletedSet);
}
