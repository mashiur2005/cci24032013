package com.cefalo.cci.dao;

import com.cefalo.cci.model.Publication;

public interface PublicationDao {
    Publication getPublication(String id);

    boolean isDuplicatePublicationExists(String publicationId, String organizationId);
}
