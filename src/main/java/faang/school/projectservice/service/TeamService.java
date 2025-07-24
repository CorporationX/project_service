package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.property.ImageProperty;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.multipart.CustomMultipartFile;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.NotImageException;
import faang.school.projectservice.exception.ResourceNotReceivedException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final ImageProperty imageProperty;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final S3Service s3Service;
    private final TeamRepository teamRepository;
    private final ResourceValidator resourceValidator;

    public void uploadAvatar(long teamId, MultipartFile file) {
        resourceValidator.validateFile(file);
        String filename = file.getOriginalFilename();
        log.info("Adding avatar to team {}: file='{}'", teamId, filename);
        checkIsImage(file);
        long currentUserId = userContext.getUserId();
        Team team = findTeamById(teamId);
        checkIsTeamMember(currentUserId, team);
        checkUserExists(currentUserId);

        MultipartFile newFile = processFile(file);
        String folder = generateFolderName(team);
        String key = generateKey(folder, newFile.getOriginalFilename());

        s3Service.uploadFile(newFile, key);

        team.setAvatarKey(key);
        teamRepository.save(team);

        log.info("Avatar added to team {}; file='{}', key={}", teamId, filename, key);
    }

    public void deleteAvatar(long teamId) {
        long currentUserId = userContext.getUserId();
        log.info("Deleting avatar from team {} by user {}", teamId, currentUserId);
        Team team = findTeamById(teamId);
        checkUserIsManager(currentUserId, team);
        checkUserExists(currentUserId);

        String key = team.getAvatarKey();
        if (key == null) {
            log.warn("Team {} don't have avatar key", teamId);
            return;
        }

        s3Service.deleteFile(key);

        team.setAvatarKey(null);
        teamRepository.save(team);

        log.info("Avatar key={} deleted from team {} by user {}", team.getAvatarKey(), teamId, currentUserId);
    }

    private Team findTeamById(long id) {
        return teamRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Team not found by id={}", id));
    }

    private void checkIsTeamMember(long userId, Team team) {
        findTeamMemberById(userId, team);
    }

    private TeamMember findTeamMemberById(long userId, Team team) {
        return team.getTeamMembers().stream()
                .filter(member -> member.getUserId() == userId)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException(
                        "User id={} is not a member of team id={}", userId, team.getId()));
    }

    private UserDto checkUserExists(long currentUserId) {
        UserDto user = userServiceClient.getUser(currentUserId);
        if (user == null) {
            throw new ResourceNotReceivedException("User not received by id={}", currentUserId);
        }
        return user;
    }

    private void checkIsImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        log.debug("Checking file '{}' is an image", filename);
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new NotImageException("File {} is not image; contentType={}", filename, contentType);
        }
    }

    private String generateFolderName(Team team) {
        String folder = "";
        if (team.getProject() != null) {
            folder += "project_" + team.getProject().getId();
        }
        folder += "/team_" + team.getId();
        log.debug("Generated folder={} for team {}", folder, team.getId());
        return folder;
    }

    private String generateKey(String folder, String filename) {
        String key = String.format("%s/%s_%s", folder, UUID.randomUUID(), filename);
        log.debug("Generated key {} for file '{}'", key, filename);
        return key;
    }

    private MultipartFile processFile(MultipartFile file) {
        log.debug("Processing file '{}'", file.getOriginalFilename());
        try {
            BufferedImage image = toBufferedImage(file);

            if (!isCorrectImageSize(image)) {
                image = resizeImage(image);
            }

            log.debug("File '{}' processed", file.getOriginalFilename());
            return toMultipartFile(image, file);
        } catch (IOException e) {
            throw new FileException("Failed processing file '{}'", file.getOriginalFilename(), e);
        }
    }

    private BufferedImage toBufferedImage(MultipartFile file) throws IOException {
        log.debug("Convert MultipartFile '{}' to BufferedImage", file.getOriginalFilename());
        return ImageIO.read(file.getInputStream());
    }

    private BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        log.debug("Resizing image: width={}, height={}", width, height);

        double scale = imageProperty.maxSideSizePx() / Math.max(width, height);

        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .scale(scale)
                .asBufferedImage();

        width = resizedImage.getWidth();
        height = resizedImage.getHeight();
        log.debug("Resized image: width={}, height={}", width, height);
        return resizedImage;
    }

    private MultipartFile toMultipartFile(BufferedImage image, MultipartFile file) throws IOException {
        log.debug("Convert BufferedImage to MultipartFile '{}'", file.getOriginalFilename());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String imageFormat = getImageFormat(file.getContentType());
        int width = image.getWidth();
        int height = image.getHeight();

        Thumbnails.of(image)
                .size(width, height)
                .outputFormat(imageFormat)
                .outputQuality(imageProperty.quality())
                .toOutputStream(outputStream);

        String safeFilename = StringUtils.cleanPath(file.getOriginalFilename());
        return CustomMultipartFile.builder()
                .name(file.getName())
                .originalFilename(safeFilename)
                .contentType(file.getContentType())
                .bytes(outputStream.toByteArray())
                .build();
    }

    private String getImageFormat(String contentType) {
        int index = contentType.indexOf("/") + 1;
        return contentType.substring(index);
    }

    private boolean isCorrectImageSize(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (Math.max(width, height) < imageProperty.maxSideSizePx()) {
            log.debug("No resize required: width={}px, height={}px", width, height);
            return true;
        }
        return false;
    }

    private void checkUserIsManager(long userId, Team team) {
        TeamMember teamMember = findTeamMemberById(userId, team);
        if (!teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new AccessDeniedException("User id={} is not a manager of team id={}", userId, team.getId());
        }
    }
}
