package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.TeamRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepositoryAdapter teamRepositoryAdapter;
    private final MinioService minioService;

    @Transactional
    public void uploadAvatar(Long teamId, MultipartFile file, Long userId) {
        Team team = teamRepositoryAdapter.getById(teamId);
        boolean inTeam = team.getTeamMembers().stream()
                .anyMatch(t -> Objects.equals(t.getUserId(), userId));
        if(!inTeam) {
            throw new DataValidateException("You're not in this team");
        }
        String fileKey = minioService.uploadFile(file);
        team.setAvatarKey(fileKey);
    }

    public InputStream getAvatar(Long teamId) {
        Team team = teamRepositoryAdapter.getById(teamId);
        return minioService.getFile(team.getAvatarKey());
    }
    @Transactional
    public void deleteAvatar(Long teamId, Long userId) {
        Team team = teamRepositoryAdapter.getById(teamId);
        boolean isManager = team.getTeamMembers().stream()
                .anyMatch(teamMember -> teamMember.getUserId().equals(userId)
                        && teamMember.getRoles().contains(TeamRole.MANAGER));

        if (!isManager) {
            throw new DataValidateException("You are not a team manager!");
        }
        minioService.deleteFile(team.getAvatarKey());
        team.setAvatarKey(null);
    }
}
