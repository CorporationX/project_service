package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.team.TeamCreateDto;
import faang.school.projectservice.dto.team.TeamEvent;
import faang.school.projectservice.filter.CustomMultipartFile;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.publisher.TeamEventPublisher;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class TeamServiceTest {

    private static final long LIMITATION_FILE_SIZE = 5 * 1024 * 1024; // 5 МБ

    @Mock
    TeamRepository teamRepository;

    @Mock
    S3Service s3Service;

    @Mock
    ResourceRepository resourceRepository;

    @Mock
    TeamMemberRepository teamMemberRepository;

    @Mock
    ResizeImagesService resizeImagesService;

    @Mock
    ProjectService projectService;

    @Mock
    TeamEventPublisher teamEventPublisher;

    @InjectMocks
    TeamService teamService;

    @Test
    public void testPositiveGetImageFromMultiPartFile() {
        Long id = 1L;
        Team team = Team.builder().build();
        BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        BufferedImage bufferedImage1 = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        byte[] contentData = new byte[]{1, 2, 3};
        CustomMultipartFile customMultipartFile = CustomMultipartFile.builder()
                .content(contentData)
                .build();
        Resource resource = Resource.builder()
                .key("test")
                .build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(resizeImagesService.getImageFromMultiPartFile(any())).thenReturn(bufferedImage);
        when(resizeImagesService.resizeImage(any(BufferedImage.class), eq(1000),
                eq(1000))).thenReturn(bufferedImage1);
        when(resizeImagesService.convertImageToMultipartFile(any(), any())).thenReturn(customMultipartFile);
        when(s3Service.uploadFile(any(), any())).thenReturn(resource);

        teamService.upload(customMultipartFile, id);

        verify(resourceRepository, times(1)).save(resource);
        verify(teamRepository, times(1)).save(team);

        assertEquals(resource.getKey(), team.getAvatarKey());
    }

    @Test
    public void testPositiveDeleteAvatar() {
        Long id = 1L;
        Team team = Team.builder()
                .id(id)
                .build();
        List<TeamMember> teamMemberList = List.of(TeamMember.builder()
                .team(Team.builder()
                        .id(id)
                        .build())
                .roles(List.of(TeamRole.MANAGER))
                .id(id).build());
        Resource resource = Resource.builder()
                .key("test")
                .build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByUserId(id)).thenReturn(teamMemberList);
        when(resourceRepository.findByKey(any())).thenReturn(resource);

        teamService.deleteAvatar(id, id);

        verify(resourceRepository, times(1)).save(resource);
        verify(teamRepository, times(1)).save(team);
        verify(s3Service, times(1)).deleteFile(any());

        assertEquals(ResourceStatus.DELETED, resource.getStatus());
    }

    @Test
    public void testNegativeGetImageFromMultipartFileLimitSize() {
        MockMultipartFile file = new MockMultipartFile("file",
                "test.jpg", "image/jpeg",
                new byte[(int) LIMITATION_FILE_SIZE + 1]);
        assertThrows(IllegalArgumentException.class, () -> teamService.upload(file, 1L));
    }

    @Test
    public void testNegativeDeleteAvatarIsNotManager() {
        Long id = 1L;

        List<TeamMember> teamMemberList = List.of(TeamMember.builder()
                .team(Team.builder()
                        .id(id)
                        .build())
                .roles(List.of(TeamRole.ANALYST))
                .id(id)
                .build());
        Team team = Team.builder()
                .id(id)
                .teamMembers((teamMemberList))
                .build();
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByUserId(id)).thenReturn(teamMemberList);

        assertThrows(NotFoundException.class, () -> teamService.deleteAvatar(id, id));
    }

    @Test
    void testPositiveAddTeamOnProject() {
        Long id = 1L;
        TeamCreateDto teamDto = createTeamDto(id);
        Project project = createProject(id, id);
        Team team = createTeam(project);
        TeamEvent teamEvent = createTeamEvent(id, project.getId(), team.getId());
        when(projectService.findById(id)).thenReturn(Optional.of(project));
        when(teamRepository.save(team)).thenReturn(team);

        teamService.addTeamOnProject(teamDto, id);

        verify(teamRepository, times(1)).save(team);
        verify(teamEventPublisher, times(1)).publish(teamEvent);
    }

    private TeamCreateDto createTeamDto(Long projectId) {
        return TeamCreateDto.builder()
                .projectId(projectId)
                .build();
    }

    private Project createProject(Long id, Long ownerId) {
        return Project.builder()
                .id(id)
                .ownerId(ownerId)
                .build();
    }

    private Team createTeam(Project project) {
        return Team.builder()
                .project(project)
                .teamMembers(List.of())
                .build();
    }

    private TeamEvent createTeamEvent(Long userId, Long projectId, Long teamId) {
        return TeamEvent.builder()
                .creatorId(userId)
                .projectId(projectId)
                .teamId(teamId)
                .build();
    }
}
