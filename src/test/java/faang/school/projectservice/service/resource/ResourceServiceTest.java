package faang.school.projectservice.service.resource;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.s3.requests.S3RequestService;
import faang.school.projectservice.validator.resource.TeamMemberResourceValidator;
import org.junit.jupiter.api.Assertions;
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
import java.util.HashSet;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private S3Service s3Service;
    @Mock
    private S3RequestService s3RequestService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamMemberResourceValidator resourceValidator;
    @InjectMocks
    private ResourceService resourceService;

    private TeamMember teamMember;
    private Project project;
    private Resource resource;
    private MultipartFile file;

    @BeforeEach
    void init() {
        file = new MockMultipartFile("name", "original_name", "TEXT", new byte[10000]);
        project = getProject();
        teamMember = getTeamMember();
        resource = getResource();

        when(teamMemberRepository.findById(teamMember.getId())).thenReturn(teamMember);
        when(resourceRepository.getReferenceById(resource.getId())).thenReturn(resource);
    }

    @Test
    public void testGetFileSuccess() {
        resourceService.getResources(teamMember.getId(), resource.getId());

        verify(teamMemberRepository, atLeastOnce()).findById(teamMember.getId());
        verify(resourceRepository, atLeastOnce()).getReferenceById(resource.getId());
        verify(resourceValidator, atLeastOnce()).validateDownloadFilePermission(teamMember, resource);
        verify(s3Service).getFile(any());
    }

    @Test
    void testFillGetFileSuccess() {
        InputStreamResource expected = new InputStreamResource(InputStream.nullInputStream());
        when(s3Service.getFile(s3RequestService.createGetRequest(resource.getKey()))).thenReturn(expected);

        InputStreamResource actual = resourceService.getResources(resource.getId(), teamMember.getId());

        assertNotNull(expected);
        Assertions.assertEquals(expected, actual);

        verify(resourceValidator, atLeastOnce()).validateDownloadFilePermission(teamMember, resource);
    }

    @Test
    void testDeleteFileSuccess() {
        var expectedResources = project.getResources().remove(resource);
        var expectedLength = project.getStorageSize().subtract(resource.getSize());
        var deleteRequest = s3RequestService.createDeleteRequest(resource.getKey());

        resourceService.deleteResource(teamMember.getId(), resource.getId());

        assertEquals(expectedResources, project.getResources().contains(resource));
        assertEquals(expectedLength, project.getStorageSize());

        assertNull(resource.getKey());
        assertNull(resource.getSize());
        assertNull(resource.getAllowedRoles());
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
        assertEquals(teamMember, resource.getUpdatedBy());

        verify(resourceRepository, atLeastOnce()).save(resource);
        verify(projectRepository, atLeastOnce()).save(project);
        verify(s3Service, atLeastOnce()).deleteFile(deleteRequest);
        verify(resourceValidator, atLeastOnce()).validateDeleteFilePermission(teamMember, resource);
    }

    private Project getProject() {
        return project = Project.builder()
                .id(1L)
                .name("Project")
                .storageSize(BigInteger.valueOf(10000))
                .maxStorageSize(BigInteger.valueOf(100000))
                .resources(new ArrayList<>(List.of(Resource.builder()
                        .id(1L)
                        .key("TestKeyName")
                        .name(file.getName())
                        .size(BigInteger.valueOf(file.getSize()))
                        .allowedRoles(new HashSet<>(getTeamMember().getRoles()))
                        .type(ResourceType.valueOf(file.getContentType()))
                        .status(ResourceStatus.ACTIVE)
                        .createdBy(getTeamMember())
                        .updatedBy(getTeamMember())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .project(project)
                        .build())))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }

    private TeamMember getTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .team(Team.builder().project(project).build())
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
    }

    private Resource getResource() {
        return Resource.builder()
                .id(1L)
                .key("TestKeyName")
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(new HashSet<>(teamMember.getRoles()))
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(project)
                .build();
    }
}