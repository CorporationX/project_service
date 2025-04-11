package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.team.TeamCreateDto;
import faang.school.projectservice.dto.team.TeamEvent;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.publisher.TeamEventPublisher;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ResizeImagesService resizeImagesService;
    private final ProjectService projectService;
    private final TeamEventPublisher teamEventPublisher;

    private final static double LIMITATION_FILE_SIZE = 5 * 1024 * 1024;

    public void upload(MultipartFile file, Long id) {
        log.info("Начало загрузки аватара для команды с ID: {}", id);

        if (file.getSize() > LIMITATION_FILE_SIZE) { // 5 МБ
            log.warn("Размер файла превышает 5 МБ: {}", file.getSize());
            throw new IllegalArgumentException("Размер файла не должен превышать 5 МБ");
        }
        Team team = teamRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Команда не найдена"));

        BufferedImage image = resizeImagesService.getImageFromMultiPartFile(file);
        BufferedImage resizedImage = resizeImagesService.resizeImage(image, image.getWidth(), image.getHeight());
        MultipartFile customFile = resizeImagesService.convertImageToMultipartFile(file, resizedImage);

        String folder = "teamAvatar" + team.getId();
        Resource resource = s3Service.uploadFile(customFile, folder);
        resourceRepository.save(resource);
        team.setAvatarKey(resource.getKey());
        teamRepository.save(team);

        log.info("Аватар успешно загружен для команды с ID: {}", id);
    }

    public void deleteAvatar(Long id, Long userId) {
        log.info("Попытка удалить аватар для команды с ID: {} пользователем с ID: {}", id, userId);

        Team team = teamRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Команда не найдена"));
        List<TeamMember> filteredMembers = teamMemberRepository.findByUserId(userId);

        boolean isManager = filteredMembers.stream()
                .anyMatch(teamMember -> teamMember.getTeam().getId().equals(team.getId())
                        && teamMember.getRoles().contains(TeamRole.MANAGER));

        if (!isManager) {
            log.warn("Пользователь с ID: {} не является менеджером команды с ID: {}", userId, id);
            throw new NotFoundException("Участник команды не в команде или не является менеджером");
        }

        Resource resource = resourceRepository.findByKey(team.getAvatarKey());

        if (resource == null) {
            log.warn("Ресурс аватара для команды с ID: {} не найден", id);
            throw new NotFoundException("Ресурс аватара не найден");
        }

        resource.setStatus(ResourceStatus.DELETED);
        team.setAvatarKey(null);
        resourceRepository.save(resource);
        s3Service.deleteFile(resource.getKey());
        teamRepository.save(team);
        log.info("Аватар успешно удален для команды с ID: {}", id);
    }

    public void addTeamOnProject(TeamCreateDto teamDto, Long userId) {
        Long projectId = teamDto.projectId();
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id %d not found", projectId));
        if (!project.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("User with id %d isn't owner project with id %d", userId, projectId);
        }
        Team team = teamRepository.save(createTeam(project));
        teamEventPublisher.publish(createTeamEvent(userId, projectId, team.getId()));
    }

    private Team createTeam(Project project) {
        return Team.builder()
                .project(project)
                .teamMembers(new ArrayList<>())
                .build();
    }

    private TeamEvent createTeamEvent(Long userId, Long projectId, Long teamId) {
        return TeamEvent.builder()
                .creatorId(userId)
                .projectId(projectId)
                .teamId(teamId)
                .build();
    }
}
