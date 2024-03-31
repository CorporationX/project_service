package faang.school.projectservice.service.cover;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;

    @SneakyThrows
    @Transactional
    public ResourceDto addCover(Long projectId, MultipartFile file) {
       // uploadFile(file);
        final Long id = file.getSize();
        ProjectDto projectDto = projectService.findProjectById(projectId);

        String folder = projectDto.getId() + projectDto.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(projectMapper.toEntity(projectDto));
        resource = resourceRepository.save(resource);

        projectDto.setCoverImageId(String.valueOf(id));


        return resourceMapper.toDto(resource);
    }

//    public void uploadFile(MultipartFile file) throws IOException {
//        if (file.isEmpty()) {
//            throw new RuntimeException("File is empty");
//        }
//        Image img = ImageIO.read(file.getInputStream());
//        int width = img.getWidth();
//        int height = img.getHeight();
//
//        if (width > height) {
//            if (width > 1080) {
//                img = resizeImage(img, 1080, (int) (1080.0 / width * height));
//            }
//        } else {
//            if (height > 1080) {
//                img = resizeImage(img, (int) (1080.0 / height * width), 1080);
//            }
//        }
//    }
//
//    private static Image resizeImage(Image img, int width, int height) {
//        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2d = resizedImage.createGraphics();
//        g2d.drawImage(img, 0, 0, width, height, null);
//        g2d.dispose();
//        return resizedImage;
//    }

}
