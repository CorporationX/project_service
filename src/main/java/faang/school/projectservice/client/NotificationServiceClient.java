package faang.school.projectservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${services.user-service.host}:${services.user-service.port}")
public interface NotificationServiceClient {
    @PostMapping("/user")
    void sendStageInvitation(@RequestBody Long teamMemberId);
}
