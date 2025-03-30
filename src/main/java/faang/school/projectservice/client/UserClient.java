package faang.school.projectservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*@FeignClient(name = "user-service", url = "http://localhost:8080", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    void setUser(@PathVariable("id") Long id);

}*/
