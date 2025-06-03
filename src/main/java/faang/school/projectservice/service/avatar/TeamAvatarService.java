package faang.school.projectservice.service.avatar;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.SizeLimitException;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamAvatarService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMapper teamMapper;
    private final S3Service s3Service;

    @Transactional
    public TeamDto createAvatar(MultipartFile avatar, Long teamId) {
        checkIfTeamExists(teamId);
        Team team = teamRepository.findById(teamId).get();
        BigInteger newStorageSize = team.getProject()
                .getStorageSize()
                .add(BigInteger.valueOf(avatar.getSize()));

        checkStorageSize(newStorageSize, team.getProject().getMaxStorageSize());
        String avatarFolder = team.getId() + team.getProject().getName();
        team.setAvatarKey(s3Service.uploadFile(avatar, avatarFolder));
        return teamMapper.toDto(teamRepository.save(team));
    }

    @Transactional
    public InputStream getTeamAvatar(Long teamId) {
        checkIfTeamExists(teamId);
        Team team = teamRepository.findById(teamId).get();
        checkIfTeamHasAvatar(team);
        return s3Service.downloadFile(team.getAvatarKey());
    }

    @Transactional
    public boolean deleteAvatar(Long teamId, Long userId) {
        checkIfTeamExists(teamId);
        Team team = teamRepository.findById(teamId).get();
        validateUserAccess(team, userId);
        return s3Service.deleteFile(team.getAvatarKey());
    }

    private void validateUserAccess(Team team, Long userId) {
        log.info("Start method validateUserAccess with userId: {}", userId);
        TeamMember teamMemberWhoCheckAcces =
                teamMemberRepository.findByUserIdAndProjectId(userId, team.getProject().getId());
        if (!checkUserAccess(teamMemberWhoCheckAcces)) {
            log.error("User with id: {} does not have access to delete avatar for team with id: {}",
                    userId, team.getId());
            throw new AccessDeniedException("User does not have access to delete avatar for this team!");
        }
        log.info("Successfully validated user access for userId: {}", userId);
    }

    private void checkIfTeamExists(Long teamId) {
        log.info("Start method checkIfTeamExists with teamId: {}", teamId);
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found with id: " + teamId));
    }

    private boolean checkUserAccess(TeamMember teamMember) {
        log.info("Start method checkUserAccess with teamMember: {}", teamMember);
        return teamMember.getRoles()
                .stream()
                .anyMatch(x -> x.equals(TeamRole.MANAGER));
    }

    private void checkStorageSize(BigInteger newSize, BigInteger maxSize) {
        log.info("Start method checkStorageSize with newSize: {} and maxSize: {}", newSize, maxSize);
        if (newSize.compareTo(maxSize) > 0) {
            log.error("Storage size exceeds the maximum limit!");
            throw new SizeLimitException("Storage size exceeds the maximum limit!");
        }
        log.info("Successfully checked storage size: {}", newSize);
    }

    private void checkIfTeamHasAvatar(Team team) {
        log.info("Start method checkIfTeamHasAvatar with teamId: {}", team.getId());
        if (Objects.isNull(team.getAvatarKey()) || team.getAvatarKey().isBlank()) {
            log.error("Team with id: {} does not have an avatar!", team.getId());
            throw new NoSuchElementException("Team does not have an avatar!");
        }
        log.info("Team with id: {} has an avatar.", team.getId());
    }
}
