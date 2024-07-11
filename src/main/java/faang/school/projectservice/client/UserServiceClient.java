package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.host}:${services.user-service.port}")
public interface UserServiceClient {

    @GetMapping("/user/{userId}")
    ProjectDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<ProjectDto> getUsersByIds(@RequestBody List<Long> ids);
}
