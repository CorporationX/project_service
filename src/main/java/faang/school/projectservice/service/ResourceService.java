package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.multipartfile.CustomMultipartFile;
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
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

        MultipartFile compressedFile = compressFile(file);

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

    private MultipartFile compressFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                    .size(512, 512)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            byte[] compressedImageBytes = outputStream.toByteArray();

            MultipartFile compressFile = new CustomMultipartFile(
                    file.getName(), file.getOriginalFilename(), file.getContentType(), compressedImageBytes);
            return compressFile;
        } catch (IOException e) {
            log.error("Failed to compress file: {}. Error: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Error compressing file %s".formatted(file), e);
        }
    }
}
