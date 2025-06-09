package faang.school.projectservice.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.AuthorizationException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamAvatarUploadServiceImpl {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final UserContext userContext;

    @Value("${team-avatar-file.maxDimension}")
    private int avatarMaxDimension;

    public void uploadFile(long teamId, MultipartFile file) {
        Team team = getTeam(teamId);
        String objectKey =s3Service.uploadFile(resizeImage(file), file.getContentType(), "team-avatars");
        team.setAvatarKey(objectKey);
        teamRepository.save(team);
        log.info("File is saved with key {}", objectKey);
    }

    public InputStream downloadFile(long teamId) {
        Team team = getTeam(teamId);
        s3Service.listContent();
        return s3Service.downloadFile(team.getAvatarKey());
    }

    public void deleteFile(long teamId) {
        List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userContext.getUserId());        
        teamMembers.stream()
            .filter(member -> member.getTeam().getId().equals(teamId))
            .filter(member -> member.getRoles().contains(TeamRole.MANAGER))
            .findFirst()
            .orElseThrow(() -> new AuthorizationException(String.format("You are not a manager of this team %d.", teamId)));

        Team team = getTeam(teamId);
        s3Service.deleteFile(team.getAvatarKey());
        team.setAvatarKey(null);
        teamRepository.save(team);
        log.info("Link to avatar is removed from team {}. File {} was removed.", teamId, team.getAvatarKey());
    }

    private File resizeImage(MultipartFile file) {
        try {
            File outputFile = new File(file.getOriginalFilename());
            Thumbnails.of(file.getInputStream())
                .size(avatarMaxDimension, avatarMaxDimension)
                .keepAspectRatio(true)
                .toFile(outputFile);
            return outputFile;
        } catch (IOException e) {
            log.error("Error while resizing a fiele, {}.", e.getMessage());
            throw new RuntimeException(String.format("Error while resizing a file, %s.", e.getMessage()));
        }
    }

    private Team getTeam(long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new EntityNotFoundException("Team not found"));
    }
}
