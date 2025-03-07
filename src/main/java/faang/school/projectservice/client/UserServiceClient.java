package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.UserDto;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service",
        url = "${services.user-service.host}:${services.user-service.port}${services.user-service.path}/users")
public interface UserServiceClient {
    @Retryable(retryFor = FeignException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);
}
