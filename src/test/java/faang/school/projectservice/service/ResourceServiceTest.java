package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.multipartfile.CustomMultipartFile;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validate.TeamMemberValidate;
import faang.school.projectservice.validate.TeamValidate;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    private static final Long TEAM_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long TEAM_MEMBER_ID = 3L;
    private static final Long RESOURCE_ID = 4L;
    private static final String TEAM_NOT_FOUND = "Team not found";
    private static final String BAN_UPLOADING = "User can not upload avatar for team";
    private static final String AVATAR_NOT_SET = "Team avatar not set";
    private static final String USER_NOT_MANAGER = "Only team managers can delete the avatar";

    @Mock private ResourceRepository resourceRepository;
    @Mock private TeamValidate teamValidate;
    @Mock private TeamMemberValidate teamMemberValidate;
    @Mock private ResourceMapper resourceMapper;
    @Mock private UserContext userContext;
    @Mock private TeamRepository teamRepository;
    @Mock private S3Service s3Service;
    @Mock private ImageCompressionService imageCompressionService;

    @InjectMocks private ResourceService resourceService;

    private Team team;
    private TeamMember teamMember;
    private Resource resource;
    private ResourceDto resourceDto;
    private MultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        team = new Team();
        team.setId(TEAM_ID);
        team.setProject(new Project());

        teamMember = new TeamMember();
        teamMember.setId(TEAM_MEMBER_ID);
        teamMember.setUserId(USER_ID);
        teamMember.setNickname("join");
        teamMember.setTeam(team);

        resource = new Resource();
        resource.setId(RESOURCE_ID);
        resource.setName("file");

        resourceDto = ResourceDto.builder()
                .id(RESOURCE_ID)
                .name("file")
                .createdBy(TEAM_MEMBER_ID)
                .build();

        mockFile = new CustomMultipartFile("file", "file.jpg", "image/jpeg", "file content".getBytes());

        lenient().when(userContext.getUserId()).thenReturn(USER_ID);
    }

    private void mockTeamValidation() {
        when(teamValidate.validateTeamById(TEAM_ID)).thenReturn(team);
    }

    private void mockTeamMemberValidation(TeamRole role) {
        teamMember.setRoles(List.of(role));
        when(teamMemberValidate.validateTeamMemberByUserIdAndByTeamId(USER_ID, TEAM_ID)).thenReturn(teamMember);
    }

    @Test
    public void testUploadAvatarTeamNotFound() {
        doThrow(new EntityNotFoundException(TEAM_NOT_FOUND)).when(teamValidate).validateTeamById(TEAM_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.uploadAvatarForTeam(TEAM_ID, mockFile));

        assertEquals(TEAM_NOT_FOUND, exception.getMessage());
        verify(teamValidate).validateTeamById(TEAM_ID);
        verifyNoInteractions(userContext, teamMemberValidate, s3Service, resourceRepository, teamRepository);
    }

    @Test
    public void testBanOnUploadingForUser() {
        mockTeamValidation();
        doThrow(new EntityNotFoundException(BAN_UPLOADING)).when(teamMemberValidate)
                .validateTeamMemberByUserIdAndByTeamId(USER_ID, TEAM_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.uploadAvatarForTeam(TEAM_ID, mockFile));

        assertEquals(BAN_UPLOADING, exception.getMessage());
        verify(teamValidate).validateTeamById(TEAM_ID);
        verify(teamMemberValidate).validateTeamMemberByUserIdAndByTeamId(USER_ID, TEAM_ID);
        verifyNoInteractions(s3Service, resourceRepository, teamRepository);
    }

    @Test
    public void testUploadFileSuccessful() {
        mockTeamValidation();
        mockTeamMemberValidation(TeamRole.MANAGER);
        when(s3Service.uploadFile(any(), any())).thenReturn(resource);
        when(resourceMapper.toResource(resource)).thenReturn(resourceDto);

        ResourceDto result = resourceService.uploadAvatarForTeam(TEAM_ID, mockFile);

        verify(s3Service).uploadFile(any(), any());
        verify(resourceRepository).save(resource);
        verify(teamRepository).save(team);
        verify(resourceMapper).toResource(resource);
        assertEquals(resourceDto, result);
    }

    @Test
    public void testDeleteAvatarTeamNotFound() {
        doThrow(new EntityNotFoundException(TEAM_NOT_FOUND)).when(teamValidate).validateTeamById(TEAM_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.deleteAvatarForTeam(TEAM_ID));

        assertEquals(TEAM_NOT_FOUND, exception.getMessage());
        verify(teamValidate).validateTeamById(TEAM_ID);
        verifyNoInteractions(userContext, teamMemberValidate, s3Service, resourceRepository, teamRepository);
    }

    @Test
    public void testDeleteAvatarNotSet() {
        mockTeamValidation();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> resourceService.deleteAvatarForTeam(TEAM_ID));

        assertEquals(AVATAR_NOT_SET, exception.getMessage());
        verify(teamValidate).validateTeamById(TEAM_ID);
        verifyNoInteractions(userContext, teamMemberValidate, s3Service, resourceRepository, teamRepository);
    }

    @Test
    public void testDeleteUserNotManager() {
        mockTeamValidation();
        mockTeamMemberValidation(TeamRole.DEVELOPER);
        team.setAvatarKey("key");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> resourceService.deleteAvatarForTeam(TEAM_ID));

        assertEquals(USER_NOT_MANAGER, exception.getMessage());
        verify(teamMemberValidate).validateTeamMemberByUserIdAndByTeamId(USER_ID, TEAM_ID);
        verifyNoInteractions(s3Service, resourceRepository, teamRepository);
    }

    @Test
    public void testDeleteAvatarForTeamSuccessful() {
        String key = "key";
        team.setAvatarKey(key);
        mockTeamValidation();
        mockTeamMemberValidation(TeamRole.MANAGER);
        when(resourceRepository.findByKey(key)).thenReturn(resource);

        resourceService.deleteAvatarForTeam(TEAM_ID);

        verify(s3Service).deleteFile(key);
        verify(resourceRepository).findByKey(key);
        verify(resourceRepository).save(resource);
        verify(teamRepository).save(team);
    }
}