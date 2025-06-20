package faang.school.projectservice.service;

import faang.school.projectservice.exception.TeamMemberRoleException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.util.ByteArrayMultipartFile;
import faang.school.projectservice.util.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamAvatarServiceImpl implements TeamAvatarService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final S3Service s3Service;

    @Override
    public String addTeamAvatar(Long userId, MultipartFile file) {
        ByteArrayInputStream resizedImageByte = ImageUtils.getResizedImageStream(file);
        MultipartFile resizedFile = convertToMultipart(resizedImageByte, file);
        String key = s3Service.uploadFile(resizedFile);
        Team team = getTeamMember(userId).getTeam();

        team.setAvatarKey(key);
        teamRepository.save(team);

        return key;
    }

    @Override
    public String removeTeamAvatar(Long userId) {
        if (!getTeamMember(userId).getRoles().contains(TeamRole.MANAGER)) {
            String errorMsg = ("User with id %d can't delete Avatar.".formatted(userId) +
                    " User with id %d must be Project Manager for Deleting Avatar".formatted(userId));
            log.error(errorMsg);
            throw new TeamMemberRoleException(errorMsg);
        }

        Team team = getTeamMember(userId).getTeam();
        String key = team.getAvatarKey();
        s3Service.deleteFile(key);
        team.setAvatarKey(null);
        teamRepository.save(team);

        return key;
    }

    private MultipartFile convertToMultipart(ByteArrayInputStream inputStream, MultipartFile file) {
        return new ByteArrayMultipartFile(inputStream.readAllBytes(), file.getName(),
                file.getOriginalFilename(), file.getContentType());
    }

    private TeamMember getTeamMember(Long userId) {
        List<TeamMember> teamMemberList = teamMemberRepository.findByUserId(userId);
        log.debug("Searching team member for user ID: {}", userId);
        if (teamMemberList.isEmpty()) {
            String errorMsg = "Team member with user id: %d was not found!"
                    .formatted(userId);
            log.error(errorMsg);
            throw new EntityNotFoundException(errorMsg);
        } else {
            return teamMemberList.get(0);
        }
    }
}