package faang.school.projectservice.service.cover;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CoverService {
    private final ResourceRepository resourceRepository;
    private final ProjectService projectService;
    private final S3Service s3Service;

    @Transactional
    private ProjectDto addCover(Long projectId, MultipartFile file){
        try {
            uploadFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ProjectDto projectDto = projectService.findProjectById(projectId);

        String folder = projectDto.getId() + projectDto.getName();
        Project cover = s3Service.uploadFile(file,folder);


    return projectDto;
    }
    public void uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        Image img = ImageIO.read(file.getInputStream());
        int width = img.getWidth();
        int height = img.getHeight();

        if (width > height) {
            if (width > 1080) {
                img = resizeImage(img, 1080, (int) (1080.0 / width * height));
            }
        } else {
            if (height > 1080) {
                img = resizeImage(img, (int) (1080.0 / height * width), 1080);
            }
        }
    }
    private static Image resizeImage(Image img, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }

}
