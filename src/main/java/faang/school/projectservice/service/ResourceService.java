package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validate.TeamMemberValidate;
import faang.school.projectservice.validate.TeamValidate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final UserContext userContext;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final ImageCompressionService imageCompressionService;
    private final TeamValidate teamValidate;
    private final TeamMemberValidate teamMemberValidate;

    @Transactional
    public ResourceDto uploadAvatarForTeam(Long teamId, MultipartFile file) {
        Team team = teamValidate.validateTeamById(teamId);

        Long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberValidate.validateTeamMemberByUserIdAndByTeamId(userId, teamId);

        MultipartFile compressedFile = imageCompressionService.compressFile(file);

        String folder = "%d%s/teamId%d/"
                .formatted(team.getProject().getId(),team.getProject().getName(), team.getId());
        Resource resource = s3Service.uploadFile(compressedFile, folder);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setProject(team.getProject());

        resourceRepository.save(resource);
        team.setAvatarKey(resource.getKey());
        teamRepository.save(team);

        return resourceMapper.toResource(resource);
    }

    @Transactional
    public void deleteAvatarForTeam(Long teamId) {
        Team team = teamValidate.validateTeamById(teamId);
        if (team.getAvatarKey() == null || team.getAvatarKey().isEmpty()) {
            log.error("Avatar for team with id {} not set", teamId);
            throw new IllegalStateException("Team avatar not set");
        }

        Long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberValidate.validateTeamMemberByUserIdAndByTeamId(userId, teamId);
        boolean isManager = teamMember.getRoles().stream()
                .anyMatch(teamRole -> teamRole.equals(TeamRole.MANAGER));

        if (!isManager) {
            log.error("A user with the manager role can delete a team avatar");
            throw new IllegalStateException("Only team managers can delete the avatar");
        }

        s3Service.deleteFile(team.getAvatarKey());

        Resource resource = resourceRepository.findByKey(team.getAvatarKey());
        resource.setStatus(ResourceStatus.DELETED);
        team.setAvatarKey(null);
        resourceRepository.save(resource);
        teamRepository.save(team);

        log.info("Avatar successful deleted for team with id {}", teamId);
    }
}
