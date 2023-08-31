package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.UpdateResourceDto;
import faang.school.projectservice.exception.InvalidCurrentUserException;
import faang.school.projectservice.exception.StorageSpaceExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import faang.school.projectservice.model.resource.Resource;
import faang.school.projectservice.model.resource.ResourceStatus;
import faang.school.projectservice.model.resource.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectFileService;
import faang.school.projectservice.util.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProjectFileServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private FileService fileService;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private ResourceMapperImpl resourceMapper;
    @InjectMocks
    private ProjectFileService projectFileService;

    private Project project;
    private Team team;
    private TeamMember teamMember;
    private Project expectedProject;
    private MockMultipartFile multipartFile;
    private Resource resource;

    @BeforeEach
    void setUp() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 8, 31, 15, 30);

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();

        team = Team.builder()
                .id(1L)
                .teamMembers(new ArrayList<>(List.of(teamMember)))
                .build();

        project = Project.builder()
                .id(1L)
                .maxStorageSize(BigInteger.valueOf(12L))
                .storageSize(BigInteger.valueOf(12L))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(createdAt)
                .teams(new ArrayList<>(List.of(team)))
                .build();

        expectedProject = Project.builder()
                .id(1L)
                .maxStorageSize(BigInteger.valueOf(12L))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(createdAt)
                .teams(new ArrayList<>(List.of(team)))
                .build();

        multipartFile = new MockMultipartFile(
                "testFile", "test.txt", "text/plain", "Test content".getBytes());

        String fileName = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();
        String key = String.format("p%d_%s_%s", 1L, size, fileName);

        resource = Resource.builder()
                .status(ResourceStatus.ACTIVE)
                .key(key)
                .type(ResourceType.TEXT)
                .size(BigInteger.valueOf(size))
                .project(project)
                .createdBy(teamMember)
                .name(fileName)
                .build();
    }

    @Test
    public void testUploadFile_Successful() {
        expectedProject.setStorageSize(BigInteger.valueOf(0L));
        ResourceDto expectedDto = ResourceDto.builder()
                .name("test.txt")
                .key("p1_12_test.txt")
                .type(ResourceType.TEXT)
                .size(BigInteger.valueOf(12L))
                .projectId(1L)
                .status(ResourceStatus.ACTIVE)
                .createdById(1L)
                .build();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "testFile", "test.txt", "text/plain", "Test content".getBytes());

        String fileName = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();
        String key = String.format("p%d_%s_%s", 1L, size, fileName);

        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);
        Mockito.when(fileService.upload(multipartFile, 1L)).thenReturn(key);

        ResourceDto resourceDto = projectFileService.uploadFile(multipartFile, 1L, 1L);

        assertEquals(expectedDto, resourceDto);
        assertEquals(expectedProject, project);
        Mockito.verify(resourceRepository, Mockito.times(1)).save(resource);
    }

    @Test
    public void testUploadFile_InvalidUser() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "testFile", "test.txt", "text/plain", "Test content".getBytes());

        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertThrows(InvalidCurrentUserException.class,
                () -> projectFileService.uploadFile(multipartFile, 1L, 2L));
    }

    @Test
    public void testUploadFile_NotEnoughStorageSpace() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "testFile", "test.txt", "text/plain", "Test content!".getBytes());

        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertThrows(StorageSpaceExceededException.class,
                () -> projectFileService.uploadFile(multipartFile, 1L, 1L));
    }

    @Test
    public void testUpdateFile_Successful() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "testFile", "test.txt", "text/plain", "Test content".getBytes());
        MockMultipartFile multipartFileUpdated = new MockMultipartFile(
                "testFile", "test.txt", "text/plain", "Test".getBytes());

        String fileName = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();
        String key = String.format("p%d_%s_%s", 1L, size, fileName);

        String fileNameUpdated = multipartFileUpdated.getOriginalFilename();
        long sizeUpdated = multipartFileUpdated.getSize();
        String keyUpdated = String.format("p%d_%s_%s", 1L, sizeUpdated, fileNameUpdated);

        Resource resource = Resource.builder()
                .id(1L)
                .status(ResourceStatus.ACTIVE)
                .key(key)
                .size(BigInteger.valueOf(size))
                .project(project)
                .createdBy(teamMember)
                .name(fileName)
                .build();

        UpdateResourceDto expectedOutput = UpdateResourceDto.builder()
                .status(ResourceStatus.ACTIVE)
                .name(fileNameUpdated)
                .createdById(1L)
                .size(BigInteger.valueOf(sizeUpdated))
                .key(keyUpdated)
                .updatedById(1L)
                .projectId(1L)
                .build();

        expectedProject.setStorageSize(BigInteger.valueOf(8L));

        Mockito.when(resourceRepository.getReferenceById(1L)).thenReturn(resource);
        Mockito.when(fileService.upload(multipartFileUpdated, 1L)).thenReturn(keyUpdated);
        Mockito.when(teamMemberRepository.findById(1L)).thenReturn(teamMember);

        UpdateResourceDto outputDto = projectFileService.updateFile(multipartFileUpdated, 1L, 1L);

        assertEquals(expectedOutput, outputDto);
        assertEquals(expectedProject, project);
    }
}
