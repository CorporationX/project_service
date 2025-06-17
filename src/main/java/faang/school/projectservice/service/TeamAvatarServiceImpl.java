package faang.school.projectservice.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import faang.school.projectservice.exception.AuthorizationException;
import faang.school.projectservice.exception.NotAllowedFileException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import faang.school.projectservice.utils.FileProcessor;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamAvatarServiceImpl implements TeamAvatarService {
    private final TeamRepository teamRepository;
    private final S3ServiceImpl s3Service;
    private final TeamMemberService teamMemberService;

    @Value("${team-avatar-file.maxSize}")
    private Long avatarMaxSizeValue;

    @Override
    public void uploadFile(long teamId, MultipartFile file) {
        checkFileContent(file);
        Team team = getTeam(teamId);
        String objectKey = s3Service.uploadFile(FileProcessor.resizeImage(file), file.getContentType(), "team-avatars");
        team.setAvatarKey(objectKey);
        teamRepository.save(team);
        log.info("File is saved with key {}", objectKey);
    }

    @Override
    public InputStream downloadFile(long teamId) {
        Team team = getTeam(teamId);
        return s3Service.downloadFile(team.getAvatarKey());
    }

    @Override
    public String getAvatarContentType(long teamId) {
        Team team = getTeam(teamId);
        return s3Service.getAvatarContentType(team.getAvatarKey());
    }

    @Override
    public void deleteFile(long teamId) {
        if (!teamMemberService.ifUserIsManager(teamId)) {
            log.error("The user is not a team {} manager.", teamId);
            throw new AuthorizationException(String.format("You are not a manager of this team %d.", teamId));
        }
        Team team = getTeam(teamId);
        s3Service.deleteFile(team.getAvatarKey());
        team.setAvatarKey(null);
        teamRepository.save(team);
        log.info("Link to avatar is removed from team {}. File {} was removed.", teamId, team.getAvatarKey());
    }

    private Team getTeam(long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new EntityNotFoundException("Team not found"));
    }

    private void checkFileContent(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error("The file is not attached or it is not an image.");
            throw new NotAllowedFileException("The file is not attached or it is not an image.");
        }

        if (file.getSize() > avatarMaxSizeValue) {
            log.error("The file is too large.");
            throw new NotAllowedFileException("The file is too large.");
        }
    }
}
