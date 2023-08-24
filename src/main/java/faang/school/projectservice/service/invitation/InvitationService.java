package faang.school.projectservice.service.invitation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationEventPublisher publisher;
}
