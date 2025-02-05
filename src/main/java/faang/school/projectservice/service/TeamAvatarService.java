package faang.school.projectservice.service;

import faang.school.projectservice.config.AppConfig;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamAvatarService {
    private final MinioService minioService;
    private final AppConfig appConfig;
    private final TeamRepository teamRepository;

    @Transactional
    public void uploadAvatar(Long teamId, MultipartFile avatar) {
        if (!avatar.getContentType().split("/")[0].equals("image")) {
            throw new IllegalArgumentException("The sent file is not an image.");
        }

        int maxSideLength = appConfig.getMaxTeamAvatarSideLength();

        Team team = teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException("Team with id " + teamId + " doesn't exists"));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                resizeImage(avatar, maxSideLength).toByteArray()
        );

        String key = "team/avatar/" + teamId + "/" + System.currentTimeMillis();
        minioService.uploadFile(inputStream,
                key,
                avatar.getContentType(),
                avatar.getSize());
        team.setAvatarKey(key);
    }

    @Transactional
    public void removeAvatar(Long teamId, Long teamMemberId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException("Team with id " + teamId + " doesn't exists"));

        TeamMember teamMember = team.getTeamMembers()
                .stream().filter(member -> member.getId() == teamMemberId).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Team member with id " + teamMemberId + " in this team not found"));

        if (!teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("Only manager can remove team's avatar");
        }

        minioService.removeFile(team.getAvatarKey());
        team.setAvatarKey(null);
    }

    private ByteArrayOutputStream resizeImage(MultipartFile multipartFile, int maxSideSize) {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        BufferedImage originalImage;

        try {
            originalImage = ImageIO.read(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("resizeImage: couldn't read the file", e);
            throw new IllegalArgumentException("couldn't read the file");
        }

        log.info("ORIGINAL FILE SIZE {}x{}", originalImage.getWidth(), originalImage.getHeight());

        String formatType = multipartFile.getContentType().split("/")[1];

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        boolean needsResize = false;
        if (width > maxSideSize || height > maxSideSize) {
            needsResize = true;
            if (width > height) {
                height = (int) (height * ((double) maxSideSize / width));
                width = maxSideSize;
            } else {
                width = (int) (width * ((double) maxSideSize / height));
                height = maxSideSize;
            }
        }

        if (!needsResize) {
            try {
                resultStream.write(multipartFile.getBytes());
            } catch (IOException e) {
                log.error("resizeImage: couldn't write the file", e);
                throw new IllegalArgumentException("couldn't write the file");
            }
            return resultStream;
        }

        BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, width, height);
        try {
            ImageIO.write(resizedImage, formatType, resultStream);
        } catch (IOException e) {
            log.error("resizeImage: couldn't write the file", e);
            throw new IllegalArgumentException("couldn't write the file");
        }

        log.info("RESIZED FILE SIZE {}x{}", resizedImage.getWidth(), resizedImage.getHeight());
        return resultStream;
    }
}
