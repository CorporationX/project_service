package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.resource.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberRepository;
    @Mock
    private ResourceValidator resourceValidator;
    @Mock
    private ResourceMapper resourceMapper;
    @InjectMocks
    private ResourceService resourceService;

    private final long userId = 1L;
    private final long resourceId = 2L;
    private TeamMember teamMember;
    private Project project;
    private Resource resource;
    private ResourceDto resourceDto;
    private MultipartFile file;

    @BeforeEach
    void init() {
        file = new MockMultipartFile("name", "original_name", "TEXT", new byte[10000]);
        project = getProject();
        teamMember = getTeamMember();
        resource = getResource();
        resourceDto = getResourceDto();
    }

    @Test
    void testSaveFileSuccess() {
        when(teamMemberRepository.findById(userId)).thenReturn(Optional.of(teamMember));
        when(amazonS3Service.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(projectRepository.save(project)).thenReturn(project);
        when(resourceMapper.toDto(resource)).thenReturn(resourceDto);

        ResourceDto actual = resourceService.saveFile(userId, file);
        assertEquals(resourceDto, actual);
    }

    @Test
    void testGetFileSuccess() {
        InputStreamResource expected = new InputStreamResource(InputStream.nullInputStream());
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(userId)).thenReturn(Optional.of(teamMember));
        when(amazonS3Service.downloadFile(resource.getKey())).thenReturn(expected);

        InputStreamResource actual = resourceService.getFile(userId, resourceId);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteFileSuccess() {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(userId)).thenReturn(Optional.of(teamMember));

        BigInteger expectedLength = project.getStorageSize().subtract(resource.getSize());

        resourceService.deleteFileFromProject(userId, resourceId);

        assertNull(resource.getKey());
        assertNull(resource.getSize());
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
        assertEquals(teamMember, resource.getUpdatedBy());
        assertEquals(expectedLength, project.getStorageSize());
    }

    private Project getProject() {
        return Project.builder()
                .id(3L)
                .storageSize(new BigInteger("1", 32))
                .resources(new ArrayList<>(List.of(new Resource())))
                .build();
    }

    private TeamMember getTeamMember() {
        return TeamMember.builder()
                .id(4L)
                .userId(userId)
                .team(Team.builder().project(project).build())
                .roles(new ArrayList<>(List.of(TeamRole.INTERN)))
                .build();
    }

    private Resource getResource() {
        return Resource.builder()
                .id(resourceId)
                .key("TestKeyName")
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(teamMember.getRoles())
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(project)
                .build();
    }

    private ResourceDto getResourceDto() {
        return ResourceDto.builder()
                .id(resourceId)
                .key("TestKeyName")
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(teamMember.getRoles())
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdById(teamMember.getId())
                .updatedById(teamMember.getId())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .projectId(project.getId())
                .build();
    }
}