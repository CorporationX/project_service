package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validation.project.ProjectValidation;
import faang.school.projectservice.validation.resource.ResourceValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    private TeamMember user;
    private Project project;
    private Team team;
    private Resource resource;
    private MultipartFile file;
    private ResourceDto resourceDto;
    private Long userId;
    private Long resourceId;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private S3Service s3Client;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidation projectValidation;
    @Mock
    private ResourceValidation resourceValidation;
    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    public void setup() {
        file = mock(MultipartFile.class);

        project = new Project();
        project.setId(5L);
        project.setName("sdfa");
        project.setDescription("sdfs");
        project.setStorageSize(BigInteger.valueOf(0));
        project.setMaxStorageSize(BigInteger.valueOf(100000));
        project.setStatus(ProjectStatus.CREATED);
        project.setVisibility(ProjectVisibility.PUBLIC);
        project.setOwnerId(1L);


        team = new Team();
        team.setId(3L);
        team.setProject(project);

        userId = 1L;
        resourceId = 1L;

        user = new TeamMember();
        user.setId(userId);
        user.setTeam(team);
        team.setTeamMembers(List.of(user));

        resource = new Resource();
        resource.setId(1L);
        resource.setName("test");
        resource.setKey("sdsdf");
        resource.setSize(BigInteger.valueOf(1000));
        resource.setProject(project);
        resource.setCreatedBy(user);
        resource.setUpdatedBy(user);

        resourceDto = new ResourceDto();
        resourceDto.setId(1L);
        resourceDto.setName("test");
        resourceDto.setSize(BigInteger.valueOf(1000));
        resourceDto.setKey("sdsdf");
    }

    @Test
    public void testUploadFileToProjectSuccess() {
        Mockito.when(teamMemberRepository.findById(userId)).thenReturn(user);
        Mockito.when(s3Client.uploadFile(any(MultipartFile.class), anyString())).thenReturn(resource);
        Mockito.when(resourceRepository.save(resource)).thenReturn(resource);
        Mockito.when(projectRepository.save(project)).thenReturn(project);
        Mockito.when(resourceMapper.toDto(resource)).thenReturn(resourceDto);

        ResourceDto resultDto = resourceService.uploadFileToProject(userId, file);

        Assertions.assertNotNull(resultDto);
        Mockito.verify(teamMemberRepository, times(1)).findById(userId);
        Mockito.verify(s3Client, times(1)).uploadFile(any(MultipartFile.class), anyString());
        Mockito.verify(resourceRepository, times(1)).save(resource);
        Mockito.verify(projectRepository, times(1)).save(project);
        Mockito.verify(resourceMapper, times(1)).toDto(resource);
        Mockito.verify(projectValidation, times(1)).projectSizeIsFull(-1);

        Assertions.assertEquals(resourceDto, resultDto);
    }

    @Test
    public void testUpdateFileFromProjectSuccess() {
        Resource rsr = new Resource();
        project.setResources(new ArrayList<>(List.of(rsr)));

        Mockito.when(teamMemberRepository.findById(userId)).thenReturn(user);
        Mockito.when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        Mockito.when(s3Client.uploadFile(any(MultipartFile.class), anyString())).thenReturn(resource);
        Mockito.when(resourceRepository.save(resource)).thenReturn(resource);
        Mockito.when(projectRepository.save(project)).thenReturn(project);
        Mockito.when(resourceMapper.toDto(resource)).thenReturn(resourceDto);

        ResourceDto resultDto = resourceService.updateFileFromProject(userId, resourceId, file);

        Assertions.assertNotNull(resultDto);

        Mockito.verify(teamMemberRepository, times(1)).findById(userId);
        Mockito.verify(resourceRepository, times(1)).findById(resourceId);
        Mockito.verify(s3Client, times(1)).uploadFile(any(MultipartFile.class), anyString());
        Mockito.verify(resourceRepository, times(1)).save(resource);
        Mockito.verify(projectRepository, times(1)).save(project);
        Mockito.verify(resourceMapper, times(1)).toDto(resource);
        Mockito.verify(projectValidation, times(1)).projectSizeIsFull(-1);
        Mockito.verify(resourceValidation, times(1)).checkingUserForUpdatingFile(user, resource);

        Assertions.assertEquals(resourceDto, resultDto);
    }

    @Test
    public void testDeleteFileFromProjectSuccess() {
        Resource rsr = new Resource();
        rsr.setName(resource.getName());
        rsr.setId(resource.getId());
        rsr.setKey(null);
        rsr.setSize(null);
        rsr.setProject(null);
        rsr.setStatus(ResourceStatus.DELETED);
        rsr.setUpdatedBy(user);
        String key = resource.getKey();
        project.setResources(new ArrayList<>(List.of(resource)));
        resourceDto.setKey(null);
        resourceDto.setSize(null);


        Mockito.when(teamMemberRepository.findById(userId)).thenReturn(user);
        Mockito.when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        project.getResources().remove(resource);
        Mockito.when(projectRepository.save(project)).thenReturn(project);
        Mockito.when(resourceRepository.save(resource)).thenReturn(rsr);
        ;

        resourceService.deleteFileFromProject(userId, resourceId);

        Mockito.verify(teamMemberRepository, times(1)).findById(userId);
        Mockito.verify(resourceRepository, times(1)).findById(resourceId);
        Mockito.verify(resourceValidation, times(1)).checkingUserForDeletingFile(user, resource);
        Mockito.verify(s3Client, times(1)).deleteFile(key);
        Mockito.verify(resourceRepository, times(1)).save(resource);
        Mockito.verify(projectRepository, times(1)).save(project);

    }
}
