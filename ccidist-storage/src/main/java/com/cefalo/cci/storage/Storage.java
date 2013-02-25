package com.cefalo.cci.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
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
	 * Retrieves an {@link InputStream} to read the data of a epub file.
	 * 
	 * @param resourceID
	 *            the unique identifier for a epub file. The URI must be the
	 *            same one as return by one of the create/modify methods of the
	 *            storage system.
	 * @return an {@link InputStream} to read the content of an epub file. May
	 *         never return <code>null</code>.
	 * @throws IOException
	 *             if an I/O error occurs. This may return
	 *             {@link FileNotFoundException} to indicate that the epub file
	 *             does not exist.
	 */
	InputStream get(final URI resourceID) throws IOException;

	/**
	 * Stores the data and returns a URI to identify the data later.
	 * 
	 * @param data
	 *            the epub file data
	 * @return a URI to identify the newly stored epub file. May never return
	 *         <code>null</code>.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	URI create(final InputStream data) throws IOException;

	/**
	 * Replaces an existing epub file with the new content.
	 * 
	 * @param resourceID
	 *            the URI of the epub file to update
	 * @param data
	 *            the new content for the epub file.
	 * @return a URI to identify the updated epub file. Please note that this
	 *         can be the same URI as before. May never return <code>null</code>
	 *         .
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	URI replace(final URI resourceID, final InputStream data)
			throws IOException;

	/**
	 * Partially updates a epub file with the provided identifier and data.
	 * 
	 * @param resourceID
	 *            the URI to identify the epub file.
	 * @param modifiedData
	 *            the modified data to update the epub file. This can be a
	 *            partial update to the epub file. What kind of partial updates
	 *            are supported is defined by the {@link Storage}
	 *            implementation.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	void update(final URI resourceID, final InputStream modifiedData)
			throws IOException;

	/**
	 * Deletes an epub file from the storage.
	 * 
	 * @param resourceID
	 *            the URI to identify the epub file
	 * @return a URI if the delete process moves the data to a "trash" location.
	 *         This may return an empty URI which denotes that the file has been
	 *         truly deleted and there is no way to acces it again. May never
	 *         return <code>null</code>.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	URI delete(final URI resourceID) throws IOException;
}
