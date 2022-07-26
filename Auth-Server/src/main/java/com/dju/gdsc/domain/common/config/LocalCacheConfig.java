package com.dju.gdsc.domain.common.config;


import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Objects;

@EnableCaching
@Configuration
// 참고 블로그 https://www.jiniaslog.co.kr/article/view?articleId=254
// https://gngsn.tistory.com/157
public class LocalCacheConfig {
    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactoryBean(){
        return new EhCacheManagerFactoryBean();
    }
    @Bean
    public EhCacheCacheManager ehCacheCacheManager(){

        // 캐시 설정
        CacheConfiguration layoutCacheConfiguration = new CacheConfiguration()
                .eternal(false)
                .timeToIdleSeconds(0)
                .timeToLiveSeconds(21600)
                .maxEntriesLocalHeap(0)
                .memoryStoreEvictionPolicy("LRU")
                .name("memberCaching");
        // 설정을 가지고 캐시 생성
        Cache layoutCache = new net.sf.ehcache.Cache(layoutCacheConfiguration);

        // 캐시 팩토리에 생성한 eh캐시를 추가
        Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(layoutCache);
        // 캐시 팩토리를 넘겨서 eh캐시 매니저 생성
        return new EhCacheCacheManager(Objects.requireNonNull(cacheManagerFactoryBean().getObject()));
    }
}
    /*
        EhCacheManagerFactoryBean	CacacheManager의 적절한 관리 및 인스턴스를 제공하는데 필요하며 EhCache 설정 리소스를 구성한다.
        maxEntriesLocalHeap	Heap 캐시 메모리 pool size 설정, GC대상이 됨.
        memoryStoreEvictionPolicy	캐시가 가득찼을때 관리 알고리즘 설정 default "LRU"
        timeToLiveSeconds	Element가 존재하는 시간. 이 시간이 지나면 캐시에서 제거된다. 이 시간이 0이면 만료 시간을 지정하지 않는다.
        timeToldleSeconds	Element가 지정한 시간 동안 사용(조회)되지 않으면 캐시에서 제거된다. 이 값이 0인 경우 조회 관련 만료 시간을 지정하지 않는다.
        eternal	true일 경우 timeout 관련 설정이 무시, element가 캐시에서 삭제되지 않음.
        name	캐시명

     */