package faang.school.projectservice.validator.project;

import faang.school.projectservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleCalendarValidator {

    private final UserServiceClient userServiceClient;

}
