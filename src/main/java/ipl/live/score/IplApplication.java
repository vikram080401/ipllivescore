package ipl.live.score;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IplApplication {
    public static void main(String[] args) {
        SpringApplication.run(IplApplication.class, args);
    }
}