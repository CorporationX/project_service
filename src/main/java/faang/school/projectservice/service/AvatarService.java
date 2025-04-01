package faang.school.projectservice.service;

import static faang.school.projectservice.contants.ErrorMessage.*;

import faang.school.projectservice.contants.InfoMessage;
import faang.school.projectservice.exception.UnsupportedFileTypeException;
import faang.school.projectservice.imageUtils.ImageUtils;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.s3.S3ServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final static long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final ResourceRepository resourceRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3ServiceImpl s3Service;

    @Transactional
    public void addAvatar(Long teamId, MultipartFile file) {
        validateFile(file);
        Team team = getTeam(teamId);

        String folder = String.format("team-%d", team.getId());
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(team.getProject());
        resourceRepository.save(resource);
        team.setAvatarKey(resource.getKey());
        teamRepository.save(team);
        log.info(InfoMessage.INFO_SAVE_FILE_SUCCESSFUL, file.getOriginalFilename());
    }

    @Transactional
    public void deleteAvatar(Long teamId, Long userId) {
        Team team = getTeam(teamId);

        if (!isUserTeamManager(team, userId)) {
            throw new IllegalArgumentException(ERROR_UNAUTHORIZED_ACCESS);
        }

        if (team.getAvatarKey() == null) {
            log.warn("No avatar found for team with ID {}. No need to delete.", teamId);
            return;
        }

        s3Service.deleteFile(team.getAvatarKey());
        team.setAvatarKey(null);
        teamRepository.save(team);
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> {
            log.error(getErrorNotFoundTeam(teamId));
            return new IllegalArgumentException(getErrorNotFoundTeam(teamId));
        });
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error(getErrorLimitSizeFile(MAX_FILE_SIZE));
            throw new IllegalArgumentException(getErrorLimitSizeFile(MAX_FILE_SIZE));
        }
        String fileExtension = getFileExtension(file);
        if (!fileExtension.equals("jpg") && !fileExtension.equals("png")) {
            log.error(ERROR_INVALID_FILE_TYPE);
            throw new UnsupportedFileTypeException(ERROR_INVALID_FILE_TYPE);
        }
        ImageUtils.compressImage(file);
    }

    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    private boolean isUserTeamManager(Team team, Long userId) {
        List<TeamMember> members = teamMemberRepository.findByUserId(userId);
        return members.stream()
                .anyMatch(member ->
                        member.getTeam().getId().equals(team.getId()) &&
                                member.getRoles().contains(TeamRole.MANAGER));
    }
}
