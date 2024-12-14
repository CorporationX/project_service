package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.publisher.projectview.ProjectViewEvent;
import faang.school.projectservice.publisher.projectview.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectViewService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectViewEventPublisher projectViewEventPublisher;

    public ProjectResponseDto viewProject(Long projectId, Long userId) {
        log.info("Starting viewProject method. Project ID: {}, User ID: {}", projectId, userId);

        Project project = projectRepository.getProjectById(projectId);
        log.info("Project retrieved successfully. Project ID: {}, Owner ID: {}", projectId, project.getOwnerId());

        if (!userId.equals(project.getOwnerId())) {
            log.info("User ID {} is viewing a project they do not own. Preparing ProjectViewEvent.", userId);
            ProjectViewEvent event = ProjectViewEvent.builder()
                    .projectId(projectId)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();
            projectViewEventPublisher.publish(event);
        } else {
            log.info("User ID {} is the owner of Project ID {}. No event will be published.", userId, projectId);
        }

        ProjectResponseDto responseDto = projectMapper.toResponseDtoFromEntity(project);
        log.info("ProjectResponseDto successfully created for Project ID: {}", projectId);

        log.info("viewProject method execution completed. Project ID: {}, User ID: {}", projectId, userId);
        return responseDto;
    }
}
