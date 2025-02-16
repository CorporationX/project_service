package faang.school.projectservice.validation;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetValidation {
    private final MeetRepository meetRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    public void validationMeet(MeetDto meet) {
        UserDto user = userServiceClient.getUser(meet.getCreatorId());
        System.out.println(user);
        Optional<Project> project = projectRepository.findById(meet.getProjectId());
        if (project.isEmpty()) {
            log.warn("Project not found with id: {}", meet.getProjectId());
            throw new ProjectNotFoundException("Project not found");
        }
        for (Long userId : meet.getUserIds()) {
            try {
                userServiceClient.getUser(userId);
            } catch (UserNotFoundException e) {
                log.warn("User not found with id : {}", userId);
                throw new UserNotFoundException("Error while retrieving user data");
            }
        }
    }

    public void meetExists(Long meetId) {
        if (!meetRepository.existsById(meetId)) {
            log.warn("Meeting not found {}", meetId);
            throw new DataNotFoundException("Meeting not found with id:" + meetId);
        }
    }

    public void permissionCheck(Long userId, Long creatorId) {
        if (!userId.equals(creatorId)) {
            log.warn("Only the meeting creator can change an appointment. UserId:{}, CreatorId:{}", userId,
                    creatorId);
            throw new SecurityException("Only the meeting creator can modify the appointment");
        }
    }
}
