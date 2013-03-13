package com.cefalo.cci.listener;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//temporary....will be removed
public class EhcacheListener implements CacheEventListener {
    private  final Logger log = LoggerFactory.getLogger(EhcacheListener.class);

    @Override
    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        System.out.println("removed...." + element.getKey());
        log.info("removed...." + element.getKey());
    }

    @Override
    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
        System.out.println("element put...." + element.getKey());
        log.info("element put...." + element.getKey());
    }

    @Override
    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void notifyElementExpired(Ehcache ehcache, Element element) {
        System.out.println("element expired...." + element.getKey());
        log.info("element expired...." + element.getKey());
    }

    @Override
    public void notifyElementEvicted(Ehcache ehcache, Element element) {
        System.out.println("element evicted...." + element.getKey());
        log.info("element evicted...." + element.getKey());

        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void notifyRemoveAll(Ehcache ehcache) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singleton instance");
	}
}
