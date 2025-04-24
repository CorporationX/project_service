package faang.school.projectservice.service;

import faang.school.projectservice.config.TeamResourceConfig;
import faang.school.projectservice.dto.client.TeamResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ResourceProcessingException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class TeamResourceServiceImpl implements TeamResourceService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final TeamResourceConfig teamResourceConfig;

    @Override
    @Transactional
    public TeamResourceDto uploadAvatar(Long teamId, MultipartFile file) {
        log.info("Uploading avatar for team: {}", teamId);

        validateAvatarFile(file, teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + teamId + " not found"));

        String oldAvatarKey = team.getAvatarKey();

        byte[] resizedImageData = resizeAndFormatImage(file);
        String avatarKey = UUID.randomUUID().toString();

        s3Service.uploadImage(resizedImageData, avatarKey, file.getContentType());
        team.setAvatarKey(avatarKey);
        teamRepository.save(team);

        if (oldAvatarKey != null) {
            s3Service.deleteImage(oldAvatarKey);
            log.info("Deleted previous avatar for team: {}", teamId);
        }

        log.info("Successfully uploaded new avatar for team: {}", teamId);

        return new TeamResourceDto(avatarKey, file.getContentType(), file.getSize(), LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteAvatar(Long teamId, Long userId) {
        log.info("Deleting avatar for team: {}", teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + teamId + " not found"));

        TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not a member of the team"));

        if (!teamMember.getRoles().contains(TeamRole.MANAGER)) {
            log.warn("User {} is not authorized to delete avatar for team: {}", userId, teamId);
            throw new IllegalArgumentException("You are not authorized to delete the avatar");
        }

        if (team.getAvatarKey() != null) {
            s3Service.deleteImage(team.getAvatarKey());
            log.info("Deleted avatar for team: {}", teamId);
        }

        team.setAvatarKey(null);
        teamRepository.save(team);
        log.info("Deleted avatar key for team: {}", teamId);
    }


    private void validateAvatarFile(MultipartFile file, Long teamId) {
        if (file == null || file.isEmpty()) {
            log.warn("Uploaded file is null or empty for team: {}", teamId);
            throw new ResourceProcessingException("Uploaded file is null or empty");
        }

        if (!teamResourceConfig.getSupportedContentTypes().contains(file.getContentType())) {
            log.warn("Unsupported content type '{}' for team: {}", file.getContentType(), teamId);
            throw new ResourceProcessingException("Only PNG and JPEG images are supported.");
        }

        if (file.getSize() > teamResourceConfig.getMaxSize()) {
            log.warn("File size exceeds the limit for team: {} (size: {})", teamId, file.getSize());
            throw new ResourceProcessingException("File size exceeds the 5 MB limit for team avatar.");
        }
    }

    private byte[] resizeAndFormatImage(MultipartFile file) {
        log.info("Resizing and formatting image: {}", file.getOriginalFilename());

        String contentType = file.getContentType();

        if (contentType == null) {
            log.error("Missing content type for file: {}", file.getOriginalFilename());
            throw new ResourceProcessingException("Missing content type for file: " + file.getOriginalFilename());
        }

        String format = contentType.substring(contentType.indexOf("/") + 1);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .size(teamResourceConfig.getWidth(), teamResourceConfig.getHeight())
                    .outputFormat(format)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error occurred while resizing and formatting the image: {}.", file.getOriginalFilename(), e);
            throw new ResourceProcessingException("Error occurred while resizing and formatting the image: "
                    + file.getOriginalFilename(), e);
        }
    }
}


