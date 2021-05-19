package container.restaurant.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import java.util.List;

@Slf4j
public class CacheEventLogger implements CacheEventListener<Object, Object> {
    public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
        Integer newValueSize = ((List) cacheEvent.getNewValue()).size();
        log.info("cache event logger message. oldValue: {} || newValueSize: {} ", cacheEvent.getOldValue(), newValueSize);
    }
}