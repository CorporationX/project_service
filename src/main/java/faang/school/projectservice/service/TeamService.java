package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void upload(MultipartFile file, Long id) {
        log.info("Начало загрузки аватара для команды с ID: {}", id);
        Team team = teamRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Команда не найдена"));
        String folder = "teamAvatar" + team.getId();
        Resource resource = s3Service.uploadFile(file, folder);
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
}
