package faang.school.projectservice.client;

import faang.school.projectservice.config.context.UserContext;
import feign.Client;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class FeignConfig {

    /*@Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }*/

    private final UserContext userContext;

    @Bean
    public RequestInterceptor authInterceptor() {
        return requestTemplate -> {
            Long userId = userContext.getUserId();
            if (userId != null) {
                requestTemplate.header("x-user-id", String.valueOf(userId));
            } else {
                log.warn("User ID is null, skipping header");
            }
            requestTemplate.header("Authorization", "Bearer some-token");
        };
    }

    //Если хочешь заменить стандартный клиент на OkHttpClient:
    /*@Bean
    public Client feignClient() {
        // Создаём OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Таймаут подключения
                .readTimeout(30, TimeUnit.SECONDS)    // Таймаут чтения
                .build();
        return new feign.okhttp.OkHttpClient(okHttpClient);
    }*/
}
  /*  @Bean
    public RequestInterceptor loggingInterceptor() {
        return requestTemplate -> {
            System.out.println("Logging request: " + requestTemplate.url());
        };
    }

    @Bean
    public RequestInterceptor customHeaderInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Custom-Header", "CustomValue");
        };
    }
}

/*
**********************************Разные клиенты*************************************
Способ 1: Создать отдельные конфигурации для каждого клиента
Если разные клиенты (например, UserClient, PostClient) должны использовать разные интерсепторы,
можно разделить конфигурации.
*
@Configuration
public class UserClientConfig {

    @Bean
    public RequestInterceptor authInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer some-token");
        };
    }
}

@Configuration
public class PostClientConfig {

    @Bean
    public RequestInterceptor loggingInterceptor() {
        return requestTemplate -> {
            System.out.println("Logging request: " + requestTemplate.url());
        };
    }

    @Bean
    public RequestInterceptor customHeaderInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Custom-Header", "CustomValue");
        };
    }
}
*
* ------------------------
* Привяжи конфигурации к клиентам
@FeignClient(name = "user-service", url = "http://localhost:8080", configuration = UserClientConfig.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    User getUser(@PathVariable("id") Long id);

    @PostMapping("/users")
    User createUser(@RequestBody User user);
}

@FeignClient(name = "post-service", url = "http://localhost:8081", configuration = PostClientConfig.class)
public interface PostClient {
    @GetMapping("/posts/{id}")
    Post getPost(@PathVariable("id") Long id);
}

*************************************************************************
Способ 4: Использовать разные клиенты для разных операций
Если методы в одном клиенте слишком разные, можно разделить их на разные интерфейсы:

@FeignClient(name = "user-service", url = "http://localhost:8080", configuration = UserClientAuthConfig.class)
public interface UserClientWithAuth {
    @GetMapping("/users/{id}")
    User getUser(@PathVariable("id") Long id);
}

@FeignClient(name = "user-service", url = "http://localhost:8080", configuration = UserClientNoAuthConfig.class)
public interface UserClientWithoutAuth {
    @PostMapping("/users")
    User createUser(@RequestBody User user);
}
Настрой конфигурации:

@Configuration
public class UserClientAuthConfig {
    @Bean
    public RequestInterceptor authInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer some-token");
        };
    }
}

@Configuration
public class UserClientNoAuthConfig {
    @Bean
    public RequestInterceptor loggingInterceptor() {
        return requestTemplate -> {
            System.out.println("Logging request: " + requestTemplate.url());
        };
    }
}
*/