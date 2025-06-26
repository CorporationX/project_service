package faang.school.projectservice.service.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.config.s3.S3Config;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static faang.school.projectservice.model.TeamRole.MANAGER;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final AmazonS3 amazonS3;
    private final S3Config s3Config;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private static final int MAX_SIZE_AVATAR = 5 * 1024 * 1024;
    private static final int MAX_SIZE = 512;

    public void uploadFile(long teamId, MultipartFile file, String fileName) {
        try {
            byte[] bytesToUpload;
            ObjectMetadata metadata = new ObjectMetadata();
            if (file.getSize() < MAX_SIZE_AVATAR) {
                bytesToUpload = file.getBytes();
            } else {
                bytesToUpload = resizeImage(file);
            }
            metadata.setContentLength(bytesToUpload.length);
            metadata.setContentType(file.getContentType());
            InputStream inputStream = new ByteArrayInputStream(bytesToUpload);
            amazonS3.putObject(s3Config.getBucketName(), fileName, inputStream, metadata);
            String url = s3Config.getEndpoint() + "/" + s3Config.getBucketName() + "/" + fileName;
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new EntityNotFoundException("Team with id: " + teamId + " not found"));
            team.setAvatarKey(url);
            teamRepository.save(team);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public byte[] getImage(String fileName) {
        S3Object s3Object = amazonS3.getObject(s3Config.getBucketName(), fileName);
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read image from S3", e);
        }
    }

    public boolean deleteImage(long managerId, String fileName) {
        TeamMember manager = teamMemberRepository.findById(managerId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Team member with id: " + managerId + " not found"));
        List<TeamRole> role = manager.getRoles()
                .stream()
                .filter(teamRole -> teamRole.equals(MANAGER)).toList();
        Team team = teamRepository.findByTeamMembers(manager);

        if (!role.isEmpty()) {
            amazonS3.deleteObject(s3Config.getBucketName(), fileName);
            team.setAvatarKey(null);
            teamRepository.save(team);
            return true;
        }
        return false;
    }

    private byte[] resizeImage(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            float scale = Math.min((float) TeamService.MAX_SIZE / width, (float) TeamService.MAX_SIZE / height);

            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);

            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to resize image", e);
        }
    }
}
