package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public ResourceDto uploadAvatarForTeam(Long teamId, MultipartFile file) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.error("Team with id {} not found", teamId);
                    return new EntityNotFoundException("Team not found");
                });

        Long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> {
                    log.error("User with id {} can not upload avatar for team with id {}", userId, teamId);
                    return new EntityNotFoundException("User can not upload avatar for team");
                });

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
}
