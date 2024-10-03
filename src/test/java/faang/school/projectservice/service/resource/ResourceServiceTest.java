package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.dto.resource.ResourceUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.util.decoder.MultiPartFileDecoder;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @InjectMocks
    private ResourceService resourceService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private S3Service s3Service;
    @Mock
    private ProjectService projectService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private ResourceValidator resourceValidator;

    private static final long RESOURCE_ID_ONE = 1L;
    private static final long PROJECT_ID_ONE = 1L;
    private static final long TEAM_MEMBER_ID_ONE = 1L;
    private static final long RESOURCE_CREATOR_NOT_VALID_ID = 5L;
    private static final BigInteger STORAGE_SIZE = new BigInteger(String.valueOf(Math.round(Math.pow(1000, 3))));
    private static final long FILE_TEST_SIZE = 100_000L;
    private static final String FILE_NAME = "BoberNotKurwa";
    private static final String NEW_FILE_NAME = "BoberNot";
    private static final String CONTENT_TYPE = "png/image";
    private static final byte[] INPUT = new byte[(int) FILE_TEST_SIZE];
    private Project project;
    private TeamMember teamMember;
    private MultiPartFileDecoder file;
    private ResourceResponseDto resourceResponseDto;
    private ResourceResponseDto updatedResponseDto;
    private Resource resource;
    private Resource updatedResource;
    private ResourceUpdateDto resourceUpdateDto;
    private S3Object s3Object;


    @BeforeEach
    void setUp() {
        List<Resource> resources = new ArrayList<>();
        resources.add(Resource.builder().build());

        project = Project.builder()
                .id(PROJECT_ID_ONE)
                .storageSize(STORAGE_SIZE)
                .resources(resources)
                .ownerId(TEAM_MEMBER_ID_ONE)
                .build();

        teamMember = TeamMember.builder()
                .id(TEAM_MEMBER_ID_ONE)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        file = MultiPartFileDecoder.builder()
                .originalFileName(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .input(INPUT)
                .build();

        resource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(file.getOriginalFilename() + "@" + BigInteger.valueOf(file.getSize()))
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();

        resourceResponseDto = ResourceResponseDto.builder()
                .id(RESOURCE_ID_ONE)
                .name(resource.getName())
                .key(resource.getKey())
                .size(resource.getSize())
                .allowedRoles(resource.getAllowedRoles())
                .type(resource.getType())
                .status(resource.getStatus())
                .createdById(teamMember.getId())
                .updatedById(teamMember.getId())
                .createdAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .projectId(project.getId())
                .build();

        resourceUpdateDto = ResourceUpdateDto.builder()
                .id(RESOURCE_ID_ONE)
                .name(NEW_FILE_NAME)
                .status(ResourceStatus.INACTIVE)
                .allowedRoles(List.of(TeamRole.DESIGNER))
                .updatedById(teamMember.getId())
                .build();

        updatedResource = Resource.builder()
                .id(RESOURCE_ID_ONE)
                .name(NEW_FILE_NAME)
                .status(ResourceStatus.INACTIVE)
                .allowedRoles(List.of(TeamRole.DESIGNER))
                .updatedBy(teamMember)
                .build();

        updatedResponseDto = ResourceResponseDto.builder()
                .id(RESOURCE_ID_ONE)
                .name(NEW_FILE_NAME)
                .status(ResourceStatus.INACTIVE)
                .allowedRoles(List.of(TeamRole.DESIGNER))
                .updatedById(teamMember.getId())
                .build();

        s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream(INPUT));
        s3Object.setObjectMetadata(new ObjectMetadata());
    }


    @Test
    @DisplayName("When valid file project id and team member id passed save file in s3 create and save resource" +
            "then return ResourceResponseDto")
    public void
    whenValidFileProjectIdAndTeamMemberIdPassedThenReturnResourceResponseDto() {
        when(projectService.getProjectById(PROJECT_ID_ONE)).thenReturn(project);
        when(teamMemberService.getTeamMemberById(TEAM_MEMBER_ID_ONE)).thenReturn(teamMember);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(resourceMapper.toResponseDto(resource)).thenReturn(resourceResponseDto);

        ResourceResponseDto responseDtoResult = resourceService.saveResource(file, PROJECT_ID_ONE, TEAM_MEMBER_ID_ONE);

        verify(resourceValidator).validateTeamMemberBelongsToProject(teamMember, project.getId());
        verify(resourceValidator).validateStorageCapacity(file, project);
        verify(resourceValidator).setNewProjectStorageSize(project);
        verify(s3Service).saveObject(file, resource);

        assertEquals(resourceResponseDto, responseDtoResult);
    }

    @Test
    @DisplayName("When valid resourceUpdateDto passed then change Resource and return resourceResponseDto")
    public void whenValidDtoPassedChangeResourceInDBThenReturnResourceResponseDto() {
        when(resourceRepository.findById(resourceUpdateDto.getId())).thenReturn(Optional.of(resource));
        when(teamMemberService.getTeamMemberById(resourceUpdateDto.getUpdatedById())).thenReturn(teamMember);
        when(resourceRepository.save(resource)).thenReturn(updatedResource);
        when(resourceMapper.toResponseDto(updatedResource)).thenReturn(updatedResponseDto);

        ResourceResponseDto responseDtoResult = resourceService.updateFileInfo(resourceUpdateDto);

        assertEquals(resourceUpdateDto.getId(), responseDtoResult.getId());
        assertEquals(resourceUpdateDto.getName(), responseDtoResult.getName());
        assertEquals(resourceUpdateDto.getStatus(), responseDtoResult.getStatus());
        assertEquals(resourceUpdateDto.getAllowedRoles().size(), responseDtoResult.getAllowedRoles().size());
        assertEquals(resourceUpdateDto.getUpdatedById(), responseDtoResult.getUpdatedById());
    }

    @Test
    @DisplayName("When resourceId and teamMemberId passed, check if it's project owner or fileOwner " +
            "then delete object in S3 storage, update resource as it's deleted")
    public void whenResourceIdAndTeamMemberIdPassedThenDeleteFileFromS3AndUpdateResource() {
        when(resourceRepository.findById(RESOURCE_ID_ONE)).thenReturn(Optional.of(resource));
        when(teamMemberService.getTeamMemberById(TEAM_MEMBER_ID_ONE)).thenReturn(teamMember);

        when(resourceRepository.save(resource)).thenReturn(resource);

        resourceService.deleteFile(RESOURCE_ID_ONE, TEAM_MEMBER_ID_ONE);

        verify(s3Service).deleteObject(resource);
    }

    @Test
    @DisplayName("When fileOwner not either project owner or file creator throws exception")
    public void whenTeamMemberPassedNeitherFileCreatorNorProjectOwnerThenThrowsException() {
        when(resourceRepository.findById(RESOURCE_ID_ONE)).thenReturn(Optional.of(resource));
        when(teamMemberService.getTeamMemberById(TEAM_MEMBER_ID_ONE)).thenReturn(teamMember);
        resource.setCreatedBy(TeamMember.builder()
                .id(RESOURCE_CREATOR_NOT_VALID_ID)
                .build());

        assertThrows(DataValidationException.class, () ->
                resourceService.deleteFile(RESOURCE_ID_ONE, TEAM_MEMBER_ID_ONE));
    }

    @Test
    @DisplayName("When non existing resource id passed then throw exception")
    public void whenNonExistingResourceIdPassedThenThrowException() {
        when(resourceRepository.findById(RESOURCE_ID_ONE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                resourceService.updateFileInfo(resourceUpdateDto));
    }

    @Test
    @DisplayName("When valid resource id passed then return downloaded file")
    public void whenValidResourceIdPassedThenReturnMultipartFile() {
        when(resourceRepository.findById(RESOURCE_ID_ONE)).thenReturn(Optional.ofNullable(resource));
        when(s3Service.getObject(resource)).thenReturn(s3Object);

        MultipartFile fileResult = resourceService.downloadFile(RESOURCE_ID_ONE);

        assertEquals(fileResult.getOriginalFilename(), resource.getKey());
    }

    @Test
    @DisplayName("When something's gone wrong while downloading file")
    public void whenSomethingGoneWrongWhileDownloadingThenThrowException() throws IOException {
        when(resourceRepository.findById(RESOURCE_ID_ONE)).thenReturn(Optional.of(resource));
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockInputStream = mock(S3ObjectInputStream.class);
        when(s3Service.getObject(resource)).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);
        when(mockInputStream.readAllBytes()).thenThrow(new IOException());

        doThrow(new IOException()).when(mockInputStream).close();
        assertThrows(RuntimeException.class, () ->
                resourceService.downloadFile(RESOURCE_ID_ONE));
    }
}
