package com.cefalo.cci.listener;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

//temporary....will be removed
public class EhcacheListenerFactory extends CacheEventListenerFactory {
    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return (CacheEventListener) new EhcacheListener();
    }
}
