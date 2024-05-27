package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validation.resource.ProjectResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectResourceServiceImplTest {

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private ProjectResourceValidator projectResourceValidator;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private  ProjectResourceServiceImpl service;

    private final long userId = 1L;
    private final long projectId = 2L;
    private final long resourceId = 3L;
    private TeamMember teamMember;
    private Project project;
    private Resource resource;
    private ResourceDto resourceDto;
    private MultipartFile file;

    @BeforeEach
    void init() {
        project = Project.builder()
                .id(projectId)
                .storageSize(new BigInteger("1", 32))
                .build();

        teamMember = TeamMember.builder()
                .id(4L)
                .userId(userId)
                .team(Team.builder().project(project).build())
                .roles(List.of(TeamRole.INTERN))
                .build();

        file = new MockMultipartFile("name", "original_name", "TEXT", new byte[10000]);


        resource = Resource.builder()
                .id(resourceId)
                .key("project/" + projectId + "/" + resourceId)
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

        resourceDto = ResourceDto.builder()
                .id(resourceId)
                .key("project/" + projectId + "/" + resourceId)
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

    @Test
    void saveFile() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(teamMember));
        when(amazonS3Service.uploadFile("project/" + projectId +"/", file)).thenReturn("key");
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDto(resource)).thenReturn(resourceDto);

        ResourceDto actual = service.saveFile(userId, projectId, file);
        assertEquals(resourceDto, actual);

        InOrder inOrder = inOrder(projectRepository, teamMemberRepository, amazonS3Service,
                resourceRepository, resourceMapper, projectResourceValidator);
        inOrder.verify(projectRepository, times(1)).getProjectById(projectId);
        inOrder.verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        inOrder.verify(projectResourceValidator, times(1)).validateMaxStorageSize(project, file.getSize());
        inOrder.verify(amazonS3Service, times(1)).uploadFile("project/" + projectId +"/", file);
        inOrder.verify(resourceRepository, times(1)).save(any(Resource.class));
        inOrder.verify(resourceMapper, times(1)).toDto(resource);
    }

    @Test
    void getFile() {
        InputStreamResource expected = new InputStreamResource(InputStream.nullInputStream());

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(teamMember));
        when(amazonS3Service.downloadFile(resource.getKey())).thenReturn(expected);

        InputStreamResource actual = service.getFile(userId, projectId, resourceId);
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(teamMemberRepository, amazonS3Service, resourceRepository);
        inOrder.verify(resourceRepository, times(1)).findById(resourceId);
        inOrder.verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        inOrder.verify(amazonS3Service, times(1)).downloadFile(resource.getKey());
    }

    @Test
    void deleteFile() {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(teamMember));

        BigInteger expectedLength = project.getStorageSize().subtract(resource.getSize());

        service.deleteFile(userId, projectId, resourceId);

        assertNull(resource.getKey());
        assertNull(resource.getSize());
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
        assertEquals(teamMember, resource.getUpdatedBy());
        assertEquals(expectedLength, project.getStorageSize());

        InOrder inOrder = inOrder(teamMemberRepository, amazonS3Service, resourceRepository, projectResourceValidator);
        inOrder.verify(resourceRepository, times(1)).findById(resourceId);
        inOrder.verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        inOrder.verify(projectResourceValidator, times(1)).validateDeletePermission(teamMember, resource);
        inOrder.verify(amazonS3Service, times(1)).deleteFile(resourceDto.getKey());
        inOrder.verify(resourceRepository, times(1)).save(resource);
    }
}