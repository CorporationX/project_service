package faang.school.projectservice.service;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TeamAvatarServiceImpl implements  TeamAvatarService {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public ResponseEntity<String> addTeamAvatar(@NotNull Long userId, @NotNull MultipartFile file) {
        TeamMember teamMember = teamMemberRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("TeamMember with user id %d was not found".formatted(userId)));

        Team team = teamMember.getTeam();

        String avatarKey = file.getOriginalFilename() + file.getSize() + file.getName();

        String folder = team.getId().toString();

        s3Service

        return null;
    }

    @Override
    public ResponseEntity<String> removeTeamAvatar(@NotNull Long userId) {
        return null;
    }
}
