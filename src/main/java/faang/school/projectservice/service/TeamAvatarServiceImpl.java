package faang.school.projectservice.service;


import faang.school.projectservice.config.AvatarConfiguration;
import faang.school.projectservice.exception.TeamMemberRoleException;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.util.ByteArrayMultipartFile;
import faang.school.projectservice.util.ImageUtils;
import faang.school.projectservice.validator.TeamMemberRoleValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;


@Service
@RequiredArgsConstructor
public class TeamAvatarServiceImpl implements TeamAvatarService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberRoleValidator teamMemberRoleValidator;
    private final TeamRepository teamRepository;
    private final AvatarConfiguration config;
    private final S3Service s3Service;
    private final static int MAX_TEAM_AVATAR_SIDE = 512;

    @Override
    public ResponseEntity<String> addTeamAvatar(@NotNull Long userId, @NotNull MultipartFile file) {

        ByteArrayInputStream resizedImageByte = ImageUtils.resizeImageToFitLongestSide(file, MAX_TEAM_AVATAR_SIDE);

        MultipartFile resizedFile = convertToMultipart(resizedImageByte, file);

        String key = s3Service.uploadFile(resizedFile);

        Team team = teamMemberRepository.findByUserId(userId).get(0).getTeam();

        team.setAvatarKey(key);

        teamRepository.save(team);

        return ResponseEntity.status(HttpStatus.CREATED).body(key);

    }

    @Override
    public ResponseEntity<String> removeTeamAvatar(@NotNull Long userId) {
        if (!teamMemberRoleValidator.isTeamManager(userId)) {
            throw new TeamMemberRoleException("User with id %d can't delete Avatar.".formatted(userId) +
                    " User with id %d must be Project Manager for Deleting Avatar".formatted(userId));
        }

        Team team = teamMemberRepository.findByUserId(userId).get(0).getTeam();

        String key = team.getAvatarKey();

        s3Service.deleteFile(key);

        team.setAvatarKey(null);

        teamRepository.save(team);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Avatar was Deleted");
    }


    private MultipartFile convertToMultipart(ByteArrayInputStream inputStream, MultipartFile file) {
        return new ByteArrayMultipartFile(inputStream.readAllBytes(), file.getName(),
                file.getOriginalFilename(), file.getContentType());
    }
}