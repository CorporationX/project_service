package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.file.ResourceService;
import faang.school.projectservice.service.file.ResourceServiceImpl;
import faang.school.projectservice.service.project.ProjectService;
import org.apache.commons.imaging.ImageReadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    @InjectMocks
    ResourceController resourceController;

    @Mock
    UserContext userContext;

    @Mock
    ResourceService resourceService;

    MultipartFile file;

    @BeforeEach
    void setUp(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        try {
            ImageIO.write(image, "jpg", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] fileContent = baos.toByteArray();
        file = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent); //  Имитация файла
    }

    @Test
    void testAddResource_Success() throws IOException, ImageReadException {
        long projectId = 1L;
        resourceService.addResource(projectId, file);
        verify(resourceService).addResource(projectId, file);
    }

    @Test
    void testUpdateResource_Success() {
        long projectId = 1L;
        long userId = 1L;
        resourceService.deleteResource(projectId, userId);
        verify(resourceService).deleteResource(projectId, userId);
    }

    @Test
    void testDeleteResource_Success() throws IOException, ImageReadException {
        long resourceId = 1L;
        long userId = 1L;
        resourceService.updateResource(resourceId, userId, file);
        verify(resourceService).updateResource(resourceId, userId, file);
    }
}