package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.mapper.ProjectCoverMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectCoverServiceImpl;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.util.project.ImageProcessor;
import faang.school.projectservice.validation.project.ProjectValidator;
import faang.school.projectservice.validation.team_member.TeamMemberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectCoverServiceImplTest {
    @InjectMocks
    private ProjectCoverServiceImpl projectCoverServiceImpl;
    @Mock
    private TeamMemberValidator teamMemberValidator;
    @Spy
    private ProjectCoverMapper projectCoverMapper = Mappers.getMapper(ProjectCoverMapper.class);
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private ImageProcessor imageProcessor;
    @Mock
    private UserContext userContext;

    @Captor
    private ArgumentCaptor<String> keyCapture;

    private Project firstProject;
    private ProjectCoverDto firstProjectCoverDto;
    private Pair<InputStream, ObjectMetadata> processedFile;
    private MockMultipartFile file;
    private final long ownerId = 1L;
    private final long teamMemberId = 2L;
    private final long firstProjectId = 1L;
    private final String path = String.format("project/cover/%d", firstProjectId);
    private final String key = String.format("project/cover/%d", firstProjectId);
    private final String oldKey = String.format("project/cover/old%d", firstProjectId);


    @BeforeEach
    void init() {
        firstProject = Project.builder()
                .id(firstProjectId)
                .name("first project")
                .build();

        firstProjectCoverDto = ProjectCoverDto.builder()
                .id(firstProjectId)
                .name("first project")
                .build();

        file = new MockMultipartFile("name", "original_name", "IMAGE", new byte[2_097_152]);


        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(2_097_152);
        metadata.setContentType("jpg");
        metadata.setLastModified(Date.from(Instant.now()));

        processedFile = Pair.of(InputStream.nullInputStream(), metadata);
    }

    private byte[] getImageBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fill(new Rectangle2D.Double(0, 0, 50, 50));
        byte[] bytes;
        try {
            ImageIO.write(image, "jpg", outputStream);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @Test
    public void testSaveShouldReturnFirstProjectCoverDto() {
        when(userContext.getUserId()).thenReturn(ownerId);
        doNothing().when(projectValidator).validateProjectIdAndCurrentUserId(firstProjectId, ownerId);
        when(imageProcessor.processCover(any(MockMultipartFile.class))).thenReturn(processedFile);
        when(amazonS3Service.uploadFile(path, processedFile)).thenReturn(key);
        when(projectRepository.findById(firstProjectId)).thenReturn(Optional.of(firstProject));
        when(projectRepository.save(firstProject)).thenReturn(firstProject);

        ProjectCoverDto actual = projectCoverServiceImpl.save(firstProjectId, file);
        ProjectCoverDto expected = firstProjectCoverDto;
        expected.setCoverImageId(key);
        expected.setUpdatedAt(actual.getUpdatedAt());

        assertEquals(expected, actual);

        InOrder inorder = inOrder(userContext, projectValidator, imageProcessor, amazonS3Service, projectRepository);
        inorder.verify(userContext, times(1)).getUserId();
        inorder.verify(projectValidator, times(1)).validateProjectIdAndCurrentUserId(anyLong(), anyLong());
        inorder.verify(imageProcessor, times(1)).processCover(any(MockMultipartFile.class));
        inorder.verify(amazonS3Service, times(1)).uploadFile(path, processedFile);
        inorder.verify(projectRepository, times(1)).findById(firstProjectId);
        inorder.verify(projectRepository, times(1)).save(firstProject);
    }


    @Test
    public void testGetByProjectIdShouldReturnInputStream() {
        InputStreamResource expected = new InputStreamResource(InputStream.nullInputStream());
        firstProject.setCoverImageId(key);

        when(userContext.getUserId()).thenReturn(ownerId);
        teamMemberValidator.validateExistenceByUserIdAndProjectId(teamMemberId, firstProjectId);
        doNothing().when(teamMemberValidator).validateExistenceByUserIdAndProjectId(firstProjectId, ownerId);
        when(projectRepository.findById(firstProjectId)).thenReturn(Optional.of(firstProject));
        when(amazonS3Service.downloadFile(key)).thenReturn(expected);

        InputStreamResource actual = projectCoverServiceImpl.getByProjectId(firstProjectId);

        assertEquals(expected, actual);

        InOrder inorder = inOrder(userContext, teamMemberValidator, projectValidator, projectRepository, amazonS3Service);
        inorder.verify(userContext, times(1)).getUserId();
        inorder.verify(teamMemberValidator, times(1)).validateExistenceByUserIdAndProjectId(anyLong(), anyLong());
        inorder.verify(projectRepository, times(1)).findById(anyLong());
        inorder.verify(amazonS3Service, times(1)).downloadFile(anyString());
    }

    @Test
    public void testChangeProjectCover() {
        firstProject.setCoverImageId(oldKey);

        when(userContext.getUserId()).thenReturn(teamMemberId);
        doNothing().when(projectValidator).validateProjectIdAndCurrentUserId(firstProjectId, teamMemberId);
        when(projectRepository.findById(firstProjectId)).thenReturn(Optional.of(firstProject));
        when(imageProcessor.processCover(any(MockMultipartFile.class))).thenReturn(processedFile);
        when(amazonS3Service.uploadFile(path, processedFile)).thenReturn(key);
        when(projectRepository.save(firstProject)).thenReturn(firstProject);

        ProjectCoverDto actual = projectCoverServiceImpl.changeProjectCover(firstProjectId, file);
        ProjectCoverDto expected = firstProjectCoverDto;
        expected.setCoverImageId(key);
        expected.setUpdatedAt(actual.getUpdatedAt());

        assertEquals(expected, actual);

        InOrder inorder = inOrder(
                userContext,
                projectValidator,
                projectRepository,
                amazonS3Service,
                imageProcessor);
        inorder.verify(userContext, times(1)).getUserId();
        inorder.verify(projectValidator, times(1)).validateProjectIdAndCurrentUserId(anyLong(), anyLong());
        inorder.verify(projectRepository, times(1)).findById(firstProjectId);
        inorder.verify(amazonS3Service, times(1)).deleteFile(keyCapture.capture());
        inorder.verify(imageProcessor, times(1)).processCover(any(MockMultipartFile.class));
        inorder.verify(amazonS3Service, times(1)).uploadFile(path, processedFile);
        inorder.verify(projectRepository, times(1)).save(firstProject);

        assertEquals(oldKey, keyCapture.getValue());
    }

    @Test
    public void testDeleteByProjectId() {
        firstProject.setCoverImageId(key);

        when(userContext.getUserId()).thenReturn(teamMemberId);
        when(projectRepository.findById(firstProjectId)).thenReturn(Optional.of(firstProject));
        when(projectRepository.save(firstProject)).thenReturn(firstProject);

        ProjectCoverDto actual = projectCoverServiceImpl.deleteByProjectId(firstProjectId);
        ProjectCoverDto expected = firstProjectCoverDto;
        expected.setUpdatedAt(actual.getUpdatedAt());

        assertEquals(expected, actual);

        InOrder inorder = inOrder(
                userContext,
                projectValidator,
                projectRepository,
                amazonS3Service);
        inorder.verify(userContext, times(1)).getUserId();
        inorder.verify(projectValidator, times(1)).validateProjectIdAndCurrentUserId(anyLong(), anyLong());
        inorder.verify(projectRepository, times(1)).findById(firstProjectId);
        inorder.verify(amazonS3Service, times(1)).deleteFile(keyCapture.capture());
        inorder.verify(projectRepository, times(1)).save(firstProject);

        assertEquals(key, keyCapture.getValue());
    }
}
