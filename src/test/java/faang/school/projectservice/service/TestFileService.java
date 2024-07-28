package faang.school.projectservice.service;

import faang.school.projectservice.amazon.AmazonS3Service;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.client.resource.MapperResource;
import faang.school.projectservice.dto.client.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.FileServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestFileService {
    @InjectMocks
    private FileServiceUpload fileService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private AmazonS3Service s3Service;
    @Mock
    private MapperResource mapperResource;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private FileServiceValidator fileServiceValidator;
    @Mock
    private UserServiceClient userServiceClient;

    private MultipartFile file;
    private long userId;
    private long resourceId;
    private long projectId;
    private Project project;
    private Resource resource;
    private TeamMember teamMember;
    private ResourceDto resourceDto;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        file = mock(MultipartFile.class);
        userId = 1L;
        resourceId = 1L;
        projectId = 1L;
        resourceDto = new ResourceDto();
        userDto = new UserDto();
        userDto.setId(1L);
        teamMember = new TeamMember();
        teamMember.setId(1L);

        project = new Project();
        project.setId(1L);
        project.setName("Name");
        project.setOwnerId(1L);
        project.setStorageSize(new BigInteger("123")); //

        resource = new Resource();
        resource.setId(1L);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setSize(new BigInteger("12345")); //
    }

    @DisplayName("Когда метод создания файла успешно отработал")
    @Test
    public void testCreateFileWhenValid() {
        String nameFolder = project.getName() + projectId;
        when(file.getSize()).thenReturn(1234L);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(teamMemberRepository.findById(userId)).thenReturn(teamMember);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(s3Service.createFile(file, nameFolder)).thenReturn(resource);
        when(mapperResource.toResourceDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = fileService.createFile(file, projectId, userId);

        assertNotNull(result);
        verify(fileServiceValidator, times(1)).checkMemoryAvailability(project.getStorageSize(), userDto);
        verify(resourceRepository, times(1)).save(resource);
        verify(projectRepository, times(1)).save(project);
    }

    @DisplayName("Если файл пытается удалить не менеджер проекта")
    @Test
    public void testDeleteFileWhenUserNotOwner() {
        project.setOwnerId(2L);
        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);
        when(projectRepository.getProjectById(resource.getProject().getId())).thenReturn(project);

        assertThrows(SecurityException.class, () -> fileService.deleteFile(resourceId, userId));

        verify(s3Service, never()).deleteFile(resource);
        verify(projectRepository, never()).save(project);
        verify(resourceRepository, never()).delete(resource);
    }

    @DisplayName("Если файл пытается удалить не создатель файла")
    @Test
    public void testDeleteFileWhenUserNotCreator() {
        teamMember.setId(2L);
        resource.setCreatedBy(teamMember);
        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);
        when(projectRepository.getProjectById(resource.getProject().getId())).thenReturn(project);

        assertThrows(SecurityException.class, () -> fileService.deleteFile(resourceId, userId));

        verify(s3Service, never()).deleteFile(resource);
        verify(projectRepository, never()).save(project);
        verify(resourceRepository, never()).delete(resource);
    }

    @DisplayName("Когда метод удаления файла успешно отработал")
    @Test
    public void testDeleteFileWhenValid() {
        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);
        when(projectRepository.getProjectById(resource.getProject().getId())).thenReturn(project);
        when(teamMemberRepository.findById(userId)).thenReturn(teamMember);

        ResponseEntity<Void> response = fileService.deleteFile(resourceId, userId);

        assertNotNull(response);
        verify(s3Service, times(1)).deleteFile(resource);
        verify(projectRepository, times(1)).save(project);
        verify(resourceRepository, times(1)).delete(resource);
    }
}