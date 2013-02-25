package com.cefalo.cci.restResource;

import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_ATOM_XML)
public class AtomFeedSupport implements MessageBodyWriter<Object> {
    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return SyndFeedImpl.class.isAssignableFrom(aClass);
    }

    @Override
    public long getSize(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        SyndFeedImpl syndFeed = (SyndFeedImpl) o;
        SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
        try {
            IOUtils.write(syndFeedOutput.outputString(syndFeed), outputStream);
        } catch (FeedException e) {
            e.printStackTrace();
        }
    }
}
