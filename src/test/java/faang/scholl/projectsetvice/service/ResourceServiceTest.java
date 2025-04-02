package faang.scholl.projectsetvice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.multipartfile.CustomMultipartFile;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.ImageCompressionService;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.service.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    private static final Long TEAM_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long TEAM_MEMBER_ID = 3L;
    private static final Long RESOURCE_ID = 4L;
    private static final String TEAM_NOT_FOUND = "Team not found";
    private static final String BAN_UPLOADING = "User can not upload avatar for team";

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceMapperImpl resourceMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private ImageCompressionService imageCompressionService;
    @InjectMocks
    private ResourceService resourceService;

    private Team team;
    private TeamMember teamMember;
    private Resource resource;
    private ResourceDto resourceDto;

    @BeforeEach
    public void setUp() throws IOException {
        team = new Team();
        team.setId(TEAM_ID);
        team.setProject(new Project());

        teamMember = new TeamMember();
        teamMember.setId(TEAM_MEMBER_ID);
        teamMember.setUserId(USER_ID);
        teamMember.setNickname("join");
        teamMember.setTeam(team);

        team.setTeamMembers(List.of(teamMember));

        resource = new Resource();
        resource.setId(RESOURCE_ID);
        resource.setName("file");
        resource.setCreatedBy(teamMember);
        resource.setProject(new Project());
        resourceDto = ResourceDto.builder()
                .id(RESOURCE_ID)
                .name("file")
                .createdBy(TEAM_MEMBER_ID)
                .build();
    }

    @Test
    public void testUploadAvatarTeamNotFound() {
        doThrow(new EntityNotFoundException(TEAM_NOT_FOUND)).when(teamRepository).findById(TEAM_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.uploadAvatarForTeam(TEAM_ID, any()));
        assertEquals(TEAM_NOT_FOUND, exception.getMessage());
        verify(teamRepository, times(1)).findById(TEAM_ID);
        verify(userContext, never()).getUserId();
        verify(teamMemberRepository, never()).findByUserIdAndTeamId(USER_ID, TEAM_ID);
        verify(s3Service, never()).uploadFile(any(), any());
        verify(resourceRepository, never()).save(any());
        verify(teamRepository, never()).save(any());
        verify(resourceMapper, never()).toResource(any());
    }

    @Test
    public void testBanOnUploadingForUser() {
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(userContext.getUserId()).thenReturn(USER_ID);

        doThrow(new EntityNotFoundException(BAN_UPLOADING))
                .when(teamMemberRepository).findByUserIdAndTeamId(USER_ID, TEAM_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.uploadAvatarForTeam(TEAM_ID, any()));
        assertEquals(BAN_UPLOADING, exception.getMessage());
        verify(teamRepository, times(1)).findById(TEAM_ID);
        verify(userContext, times(1)).getUserId();
        verify(teamMemberRepository, times(1))
                .findByUserIdAndTeamId(USER_ID, TEAM_ID);
        verify(s3Service, never()).uploadFile(any(), any());
        verify(resourceRepository, never()).save(any());
        verify(teamRepository, never()).save(any());
        verify(resourceMapper, never()).toResource(any());
    }

    @Test
    public void testUploadFileSuccessful() throws IOException {
        byte[] fileContent = "file content".getBytes();
        MultipartFile mockFile = new CustomMultipartFile(
                "file", "file.jpg", "image/jpeg", fileContent);

        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndTeamId(USER_ID, TEAM_ID))
                .thenReturn(Optional.of(teamMember));
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(resource);
        when(resourceMapper.toResource(resource)).thenReturn(resourceDto);
        when(imageCompressionService.compressFile(any(MultipartFile.class)))
                .thenReturn(mockFile);
        ResourceDto resultDtoResource = resourceService.uploadAvatarForTeam(TEAM_ID, mockFile);

        verify(teamRepository, times(1)).findById(TEAM_ID);
        verify(userContext, times(1)).getUserId();
        verify(teamMemberRepository, times(1))
                .findByUserIdAndTeamId(USER_ID, TEAM_ID);
        verify(s3Service, times(1))
                .uploadFile(any(MultipartFile.class), anyString());
        verify(resourceRepository, times(1)).save(resource);
        verify(teamRepository, times(1)).save(team);
        verify(resourceMapper, times(1)).toResource(resource);
        assertEquals(resultDtoResource.getId(), resourceDto.getId());
        assertEquals(resultDtoResource.getName(), resourceDto.getName());
        assertEquals(resultDtoResource.getCreatedBy(), resourceDto.getCreatedBy());
    }
}
