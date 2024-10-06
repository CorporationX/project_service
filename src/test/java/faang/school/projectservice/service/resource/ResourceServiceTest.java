package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.resource.ResourceDto;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenAccessException;
import faang.school.projectservice.exception.StorageSizeExceededException;
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
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    private final static long MANAGER_ID = 1L;
    private final static long DEVELOPER_ID = 2L;
    private final static long INTERN_ID = 3L;
    private final static long PROJECT_ID = 1L;
    private final static long RESOURCE_ID = 1L;
    private final static long NOT_TEAM_MEMBER_ID = 5L;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);

    @Mock
    private UserContext userContext;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Project project;
    private Resource resource;
    private TeamMember manager;
    private TeamMember developer;
    private TeamMember intern;
    private ResourceResponseObject resourceResponseObject;
    private MultipartFile multipartFile;

    @BeforeEach
    public void setUp() {
        manager = TeamMember.builder()
                .userId(MANAGER_ID)
                .roles(List.of(TeamRole.MANAGER))
                .build();

        developer = TeamMember.builder()
                .userId(DEVELOPER_ID)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        intern = TeamMember.builder()
                .userId(INTERN_ID)
                .roles(List.of(TeamRole.INTERN))
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(developer))
                .build();

        project = Project.builder()
                .id(PROJECT_ID)
                .maxStorageSize(BigInteger.TEN)
                .storageSize(BigInteger.ONE)
                .teams(List.of())
                .build();

        team.setTeamMembers(List.of(developer));
        team.setProject(project);

        resource = Resource.builder()
                .id(RESOURCE_ID)
                .key("key1")
                .size(BigInteger.ONE)
                .type(ResourceType.IMAGE)
                .allowedRoles(List.of(TeamRole.DEVELOPER))
                .project(project)
                .createdBy(developer)
                .build();

        resourceResponseObject = new ResourceResponseObject(InputStream.nullInputStream(), "image/jpeg");
        multipartFile = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("Получение ресурса по id менеджером проекта")
    public void testSuccessGetResourceByManager() {
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID)).thenReturn(manager);
        when(s3Service.downloadFile(resource.getKey())).thenReturn(resourceResponseObject);

        ResourceResponseObject result = resourceService.getResourceById(RESOURCE_ID);

        assertEquals(resourceResponseObject, result);

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID);
        verify(s3Service).downloadFile(resource.getKey());
    }

    @Test
    @DisplayName("Получение ресурса по id участником команды с разрешнной ролью")
    public void testSuccessGetResourceByTeamMemberWithAllowedRole() {
        when(userContext.getUserId()).thenReturn(DEVELOPER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID)).thenReturn(developer);
        when(s3Service.downloadFile(resource.getKey())).thenReturn(resourceResponseObject);

        ResourceResponseObject result = resourceService.getResourceById(RESOURCE_ID);

        assertEquals(resourceResponseObject, result);

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID);
        verify(s3Service).downloadFile(resource.getKey());
    }

    @Test
    @DisplayName("Получение ресурса по id участником команды с не разрешенной ролью")
    public void testGetResourceByTeamMemberWithNotAllowedRole() {
        when(userContext.getUserId()).thenReturn(INTERN_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(INTERN_ID, PROJECT_ID)).thenReturn(intern);
        assertThrows(ForbiddenAccessException.class, () -> resourceService.getResourceById(RESOURCE_ID));

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(INTERN_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Получение ресурса с несуществующим id")
    public void testGetResourceWithNonExistedId() {
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> resourceService.getResourceById(RESOURCE_ID));
    }

    @Test
    @DisplayName("Получение ресурса по id не участником команды")
    public void testGetResourceByNotTeamMember() {
        when(userContext.getUserId()).thenReturn(NOT_TEAM_MEMBER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID)).thenReturn(null);
        assertThrows(ForbiddenAccessException.class, () -> resourceService.getResourceById(RESOURCE_ID));

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Удаление ресурса по id менеджером проекта")
    public void testSuccessDeleteResourceByManager() {
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID)).thenReturn(manager);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(projectRepository.save(project)).thenReturn(project);

        resourceService.deleteResourceById(RESOURCE_ID);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID);
        verify(resourceRepository).save(resource);
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("Удаление ресурса по id создателем")
    public void testSuccessDeleteResourceByCreator() {
        when(userContext.getUserId()).thenReturn(DEVELOPER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID)).thenReturn(developer);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(projectRepository.save(project)).thenReturn(project);
        String key = resource.getKey();
        resourceService.deleteResourceById(RESOURCE_ID);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID);
        verify(resourceRepository).save(resource);
        verify(projectRepository).save(project);
        verify(s3Service).deleteFile(key);
    }

    @Test
    @DisplayName("Удаление ресурса по id участником команды")
    public void testDeleteResourceByTeamMember() {
        when(userContext.getUserId()).thenReturn(INTERN_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(INTERN_ID, PROJECT_ID)).thenReturn(intern);

        assertThrows(ForbiddenAccessException.class, () -> resourceService.deleteResourceById(RESOURCE_ID));

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(INTERN_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Удаление ресурса по id не участником команды")
    public void testDeleteResourceByNotTeamMember() {
        when(userContext.getUserId()).thenReturn(NOT_TEAM_MEMBER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID)).thenReturn(null);

        assertThrows(ForbiddenAccessException.class, () -> resourceService.deleteResourceById(RESOURCE_ID));

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Обновление ресурса по id менеджером проекта")
    public void testUpdateSuccessUpdateResourceByManager() {
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID)).thenReturn(manager);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(projectRepository.save(project)).thenReturn(project);
        when(multipartFile.getSize()).thenReturn(5L);
        when(multipartFile.getOriginalFilename()).thenReturn("name");

        BigInteger oldSize = project.getStorageSize();
        ResourceDto result = resourceService.updateResourceById(RESOURCE_ID, multipartFile);

        assertNotEquals(oldSize, project.getStorageSize());
        assertEquals(BigInteger.valueOf(5L), project.getStorageSize());
        assertEquals("name", result.name());
        assertEquals(resource.getId(), result.id());

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID);
        verify(resourceRepository).save(resource);
        verify(projectRepository).save(project);
        verify(s3Service).uploadFile(multipartFile, resource.getKey());
    }

    @Test
    @DisplayName("Обновление ресурса по id создателем файла")
    public void testUpdateSuccessUpdateResourceByCreator() {
        when(userContext.getUserId()).thenReturn(DEVELOPER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID)).thenReturn(developer);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(projectRepository.save(project)).thenReturn(project);
        when(multipartFile.getSize()).thenReturn(5L);
        when(multipartFile.getOriginalFilename()).thenReturn("name");

        BigInteger oldSize = project.getStorageSize();
        ResourceDto result = resourceService.updateResourceById(RESOURCE_ID, multipartFile);

        assertNotEquals(oldSize, project.getStorageSize());
        assertEquals(BigInteger.valueOf(multipartFile.getSize()), project.getStorageSize());
        assertEquals(multipartFile.getOriginalFilename(), result.name());
        assertEquals(resource.getId(), result.id());

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID);
        verify(resourceRepository).save(resource);
        verify(projectRepository).save(project);
        verify(s3Service).uploadFile(multipartFile, resource.getKey());
    }

    @Test
    @DisplayName("Обновление ресурса по id участником команды")
    public void testUpdateResourceByTeamMember() {
        when(userContext.getUserId()).thenReturn(INTERN_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(INTERN_ID, PROJECT_ID)).thenReturn(intern);

        assertThrows(ForbiddenAccessException.class, () -> resourceService.updateResourceById(RESOURCE_ID, multipartFile));

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(INTERN_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Обновление ресурса по id не участником команды")
    public void testUpdateResourceByNotTeamMember() {
        when(userContext.getUserId()).thenReturn(NOT_TEAM_MEMBER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID)).thenReturn(intern);

        assertThrows(ForbiddenAccessException.class, () -> resourceService.updateResourceById(RESOURCE_ID, multipartFile));

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Обновление ресурса по id с превышением максимального размера хранилища")
    public void testUpdateResourceWithExceedStorageSize() {
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID)).thenReturn(manager);
        when(multipartFile.getSize()).thenReturn(11L);

        assertThrows(StorageSizeExceededException.class, () -> resourceService.updateResourceById(RESOURCE_ID, multipartFile));

        verify(userContext).getUserId();
        verify(resourceRepository).findById(RESOURCE_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(MANAGER_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Создание ресурса в проекте участником команды")
    public void testSuccessCreateResourceByTeamMember() {
        String name = "name";
        long size = 5L;
        String key = "key";
        String contentType = "image/jpeg";
        Resource resourceToCreate = Resource.builder()
                .name(name)
                .size(BigInteger.valueOf(size))
                .type(ResourceType.getResourceType(contentType))
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .key(key)
                .createdBy(developer)
                .updatedBy(developer)
                .allowedRoles(developer.getRoles())
                .build();

        ResourceDto resourceDto = resourceMapper.toResourceDto(resourceToCreate);
        when(userContext.getUserId()).thenReturn(DEVELOPER_ID);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resourceToCreate);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID)).thenReturn(developer);
        when(projectRepository.save(project)).thenReturn(project);
        when(multipartFile.getOriginalFilename()).thenReturn(name);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getSize()).thenReturn(size);

        ResourceDto result = resourceService.addResourceToProject(PROJECT_ID, multipartFile);
        assertEquals(resourceDto.id(), result.id());

        verify(userContext).getUserId();
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(resourceRepository).save(any(Resource.class));
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(DEVELOPER_ID, PROJECT_ID);
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("Создание ресурса в проекте не участником команды")
    public void testCreateResourceByNotTeamMember() {
        when(userContext.getUserId()).thenReturn(NOT_TEAM_MEMBER_ID);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID)).thenReturn(null);

        assertThrows(ForbiddenAccessException.class, () -> resourceService.addResourceToProject(PROJECT_ID, multipartFile));

        verify(userContext).getUserId();
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(teamMemberJpaRepository).findByUserIdAndProjectId(NOT_TEAM_MEMBER_ID, PROJECT_ID);
    }

    @Test
    @DisplayName("Создание ресурса в проекте с превышением максимального размера хранилища")
    public void testCreateResourceExceedStorageSize() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(multipartFile.getSize()).thenReturn(11L);

        assertThrows(StorageSizeExceededException.class, () -> resourceService.addResourceToProject(PROJECT_ID, multipartFile));

        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(multipartFile).getSize();
    }
}