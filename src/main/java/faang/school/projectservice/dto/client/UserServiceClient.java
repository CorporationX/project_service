package faang.school.projectservice.dto.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user_service")
public interface UserServiceClient {

}
