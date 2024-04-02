package faang.school.projectservice.service.resource;

import faang.school.projectservice.image.ImageResizer;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private S3Service s3Service;
    @Mock
    private ImageResizer imageResizer;
    @InjectMocks
    private ResourceService resourceService;

    private Resource resource;
    private Project project;
    private MultipartFile file;

    @SneakyThrows
    @Test
    void testAddCoverToTheProject() {
        project = Project.builder()
                .id(1L)
                .coverImageId("Image: " + 1L)
                .build();
        Long id = 1L;
        BufferedImage resizeFile = new BufferedImage(1080, 1090, BufferedImage.TYPE_INT_RGB);
        when(imageResizer.resizeAndCompressImage(any(MultipartFile.class))).thenReturn(any(File.class));

        Resource resourse = resourceService.addACoverToTheProject(project.getId(), file);

        assertNotNull(resourse );
    }
}
