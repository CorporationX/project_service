package faang.school.projectservice.service.team;

import faang.school.projectservice.config.minio.ImageFormat;
import faang.school.projectservice.config.minio.properties.TeamAvatarProperties;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.TeamRepositoryAdapter;
import faang.school.projectservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamAvatarService {

    private final TeamRepositoryAdapter teamRepositoryAdapter;
    private final MinioService minioService;
    private final TeamAvatarProperties teamAvatarProperties;

    @Transactional
    public void uploadAvatar(Long teamId, MultipartFile file, Long userId) {
        Team team = teamRepositoryAdapter.getById(teamId);
        boolean inTeam = team.getTeamMembers().stream()
                .anyMatch(t -> Objects.equals(t.getUserId(), userId));
        if (!inTeam) {
            throw new DataValidationException("You're not in this team");
        }
        String fileKey = uploadFile(file);
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
            throw new DataValidationException("You are not a team manager!");
        }
        minioService.deleteFile(team.getAvatarKey());
        team.setAvatarKey(null);
    }

    private String uploadFile(MultipartFile file) {
        long fileSize = file.getSize();
        if (fileSize > teamAvatarProperties.getMaxFileSize()) {
            throw new DataValidationException("The file exceeds the allowed size");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new DataValidationException("Only images can be uploaded!");
        }

        String uniqueKey = String.format("%s/%s-%s", teamAvatarProperties.getFolderName(), UUID.randomUUID(),
                file.getOriginalFilename());
        ImageFormat imageFormat = ImageFormat.fromContentType(contentType);
        InputStream processedStream;
        try {
            processedStream = compressImage(file.getInputStream(), imageFormat.getFormat());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        minioService.uploadFile(uniqueKey, processedStream, fileSize, contentType);

        return uniqueKey;
    }

    private InputStream compressImage(InputStream inputStream, String contentType) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("Error reading image!");
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int newWidth, newHeight;

        if (width > height) {
            newWidth = Math.min(width, teamAvatarProperties.getMaxImageSize());
            newHeight = (newWidth * height) / width;
        } else {
            newHeight = Math.min(height, teamAvatarProperties.getMaxImageSize());
            newWidth = (newHeight * width) / height;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(newWidth, newHeight)
                .outputFormat(contentType)
                .outputQuality(0.8)
                .toOutputStream(outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
