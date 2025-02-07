package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.UserServiceConnectionException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.host}:${services.user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/v1/user/{userId}")
    UserDto getUser(@PathVariable long userId) throws UserServiceConnectionException;

    @PostMapping("/api/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);
}
