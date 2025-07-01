package faang.school.projectservice.service.minio;

import faang.school.projectservice.client.s3.S3Client;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static faang.school.projectservice.model.TeamRole.MANAGER;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final S3Client s3Client;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void uploadFile(long teamId, MultipartFile file, String fileName) {
        String url = s3Client.uploadFile(file, fileName);
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new EntityNotFoundException("Team with id: " + teamId + " not found"));
        team.setAvatarKey(url);
        teamRepository.save(team);
    }

    public boolean deleteImage(long managerId, String fileName) {
        TeamMember manager = teamMemberRepository.findById(managerId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Team member with id: " + managerId + " not found"));
        boolean isManager = manager.getRoles().stream().anyMatch(teamRole -> teamRole.equals(MANAGER));
        if (!isManager) return false;
        Team team = teamRepository.findByTeamMembers(manager);
        s3Client.deleteFile(fileName);
        team.setAvatarKey(null);
        teamRepository.save(team);
        return true;
    }
}
