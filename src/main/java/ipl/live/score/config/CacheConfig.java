package ipl.live.score.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("liveScore");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(16, TimeUnit.MINUTES) // 🔥 16 mins TTL
                        .maximumSize(100)
        );
        return cacheManager;
    }
}