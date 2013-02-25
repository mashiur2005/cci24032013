package com.cefalo.cci.storage;

import java.io.InputStream;
import java.net.URI;

/**
 * The base interface to implement epub file storage facilities. The interface
 * does not assume any concrete storage medium. All return values are specific
 * to the storage implementation. The returned {@link URI}s from the methods
 * must be used as such by any users.
 * 
 * @author partha
 * 
 */
public interface Storage {
	/**
	 * Stores the data and returns a URI to identify the data later.
	 * 
	 * @param data
	 *            the epub file data
	 * @return a URI to identify the newly stored epub file. May never return
	 *         <code>null</code>.
	 */
	URI create(final InputStream data);

	/**
	 * Replaces an existing epub file with the new content.
	 * 
	 * @param dataURI
	 *            the URI of the epub file to update
	 * @param data
	 *            the new content for the epub file.
	 * @return a URI to identify the updated epub file. Please note that this
	 *         can be the same URI as before. May never return <code>null</code>
	 *         .
	 */
	URI replace(final URI dataURI, final InputStream data);

	/**
	 * Partially updates a epub file with the provided identifier and data.
	 * 
	 * @param dataURI
	 *            the URI to identify the epub file.
	 * @param modifiedData
	 *            the modified data to update the epub file. This can be a
	 *            partial update to the epub file. What kind of partial updates
	 *            are supported is defined by the {@link Storage}
	 *            implementation.
	 */
	void update(final URI dataURI, final InputStream modifiedData);

	/**
	 * Deletes an epub file from the storage.
	 * 
	 * @param dataURI
	 *            the URI to identify the epub file
	 * @return a URI if the delete process moves the data to a "trash" location.
	 *         This may return an empty URI which denotes that the file has been
	 *         truly deleted and there is no way to acces it again. May never
	 *         return <code>null</code>.
	 */
	URI delete(final URI dataURI);
}
