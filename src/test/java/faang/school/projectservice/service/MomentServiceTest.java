package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.moment.MomentRequestDto;
import faang.school.projectservice.dto.client.moment.MomentResponseDto;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @Mock
    TeamMemberRepository teamMemberRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    MomentRepository momentRepository;
    @Spy
    MomentMapperImpl momentMapper;

    @InjectMocks
    MomentService momentService;


    @Test
    void testAddNew_projectsNullMembersNotNull() {
        Long creatorId = 999L;
        Long teamMember1Id = 1L;
        Long teamMember1ProjectId = 11L;
        Project teamMember1Project = Project.builder()
                .id(teamMember1ProjectId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(null)
                .teamMemberIds(List.of(teamMember1Id))
                .build();

        when(projectRepository.findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds()))
                .thenReturn(List.of(teamMember1Project));
        when(momentRepository.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getTeamMemberIds().contains(teamMember1Id));
        assertTrue(momentResponseDto.getProjectIds().contains(teamMember1ProjectId));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(teamMemberRepository, times(1))
                .checkExistAll(momentRequestDto.getTeamMemberIds());
        verify(projectRepository, times(1))
                .findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds());
        verify(momentRepository, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNullMembersNotNull_memberNotExist() {
        long creatorId = 999L;
        Long teamMember1Id = 1L;
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(null)
                .teamMemberIds(List.of(teamMember1Id))
                .build();

        doThrow(new NotFoundException(ErrorMessage.SOME_OF_MEMBERS_NOT_EXIST))
                .when(teamMemberRepository).checkExistAll(momentRequestDto.getTeamMemberIds());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.SOME_OF_MEMBERS_NOT_EXIST.getMessage(), exception.getMessage());

        verify(teamMemberRepository, times(1))
                .checkExistAll(momentRequestDto.getTeamMemberIds());
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNullMembersNotNull_projectStatusInvalid() {
        long creatorId = 999L;
        Long teamMember1Id = 1L;
        Long teamMember1ProjectId = 11L;
        Project teamMember1Project = Project.builder()
                .id(teamMember1ProjectId)
                .status(ProjectStatus.COMPLETED)
                .build();
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(null)
                .teamMemberIds(List.of(teamMember1Id))
                .build();


        when(projectRepository.findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds()))
                .thenReturn(List.of(teamMember1Project));

        ConflictException exception = assertThrows(ConflictException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.PROJECT_STATUS_INVALID.getMessage(), exception.getMessage());

        verify(teamMemberRepository, times(1))
                .checkExistAll(momentRequestDto.getTeamMemberIds());
        verify(projectRepository, times(1))
                .findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds());
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNotNullMembersNull() {
        Long creatorId = 999L;
        Long project1Id = 1L;
        Project project1 = Project.builder()
                .id(project1Id)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        List<Long> projectIds = List.of(project1Id);
        List<Project> projects = List.of(project1);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(projectIds)
                .teamMemberIds(null)
                .build();
        Long project1TeamMember1Id = 11L;
        List<Long> project1TeamMemberIds = List.of(project1TeamMember1Id);

        when(projectRepository.findAllByIds(projectIds)).thenReturn(projects);
        when(teamMemberRepository.findIdsByProjectIds(projectIds)).thenReturn(project1TeamMemberIds);
        when(momentRepository.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getProjectIds().contains(project1Id));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(project1TeamMember1Id));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(projectRepository, times(1)).findAllByIds(projectIds);
        verify(teamMemberRepository, times(1)).findIdsByProjectIds(projectIds);
        verify(momentRepository, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNotNullMembersNotNull_AllMembersFitProjects() {
        long creatorId = 999L;
        Long teamMember1Id = 1L;
        List<Long> teamMemberIds = new ArrayList<>(List.of(teamMember1Id));
        Long project1Id = 1L;
        Project project1 = Project.builder()
                .id(project1Id)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        List<Long> projectIds = List.of(project1Id);
        List<Project> projects = List.of(project1);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(projectIds)
                .teamMemberIds(teamMemberIds)
                .build();

        when(projectRepository.findAllByIds(projectIds)).thenReturn(projects);
        when(teamMemberRepository.findIdsByProjectIds(projectIds)).thenReturn(teamMemberIds);
        when(projectRepository.findAllDistinctByTeamMemberIds(teamMemberIds)).thenReturn(projects);
        when(teamMemberRepository.findIdsByProjectIds(List.of())).thenReturn(List.of());
        when(momentRepository.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getProjectIds().contains(project1Id));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(teamMember1Id));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(projectRepository, times(1)).findAllByIds(projectIds);
        verify(teamMemberRepository, times(1)).findIdsByProjectIds(projectIds);
        verify(momentRepository, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNotNullMembersNotNull_MembersUnFitProjects() {
        long creatorId = 999L;
        Long teamMember1Id = 1L;
        List<Long> teamMemberIds = new ArrayList<>(List.of(teamMember1Id));
        List<Long> anotherTeamMemberIds = List.of(2L);
        Long project1Id = 1L;
        Project project1 = Project.builder()
                .id(project1Id)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        List<Long> projectIds = List.of(project1Id);
        List<Project> projects = List.of(project1);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(projectIds)
                .teamMemberIds(teamMemberIds)
                .build();

        when(projectRepository.findAllByIds(projectIds)).thenReturn(projects);
        when(teamMemberRepository.findIdsByProjectIds(projectIds)).thenReturn(anotherTeamMemberIds);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> momentService.addNew(momentRequestDto, creatorId)
        );
        assertEquals(ErrorMessage.MEMBERS_UNFIT_PROJECTS.getMessage(), exception.getMessage());

        verify(projectRepository, times(1)).findAllByIds(projectIds);
        verify(teamMemberRepository, times(1)).findIdsByProjectIds(projectIds);
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNotNullMembersNotNull_HavingEmptyProjects() {
        long creatorId = 999L;
        Long teamMember1Id = 1L;
        List<Long> teamMemberIds = new ArrayList<>((List.of(teamMember1Id)));
        Long project1Id = 1L;
        Long project2Id = 2L;
        Project project1 = Project.builder()
                .id(project1Id)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project project2 = Project.builder()
                .id(project2Id)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        List<Long> projectIds = List.of(project1Id, project2Id);
        List<Project> projects = List.of(project1, project2);
        List<Project> filledProjects = List.of(project1);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(projectIds)
                .teamMemberIds(teamMemberIds)
                .build();
        Long missingTeamMember1Id = 2L;
        List<Long> missingTeamMemberIds = List.of(missingTeamMember1Id);

        when(projectRepository.findAllByIds(projectIds)).thenReturn(projects);
        when(teamMemberRepository.findIdsByProjectIds(projectIds)).thenReturn(teamMemberIds);
        when(projectRepository.findAllDistinctByTeamMemberIds(teamMemberIds)).thenReturn(filledProjects);
        when(teamMemberRepository.findIdsByProjectIds(List.of(project2Id))).thenReturn(missingTeamMemberIds);
        when(momentRepository.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getProjectIds().containsAll(projectIds));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(teamMember1Id));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(missingTeamMember1Id));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(projectRepository, times(1)).findAllByIds(projectIds);
        verify(teamMemberRepository, times(1)).findIdsByProjectIds(projectIds);
        verify(projectRepository, times(1)).findAllDistinctByTeamMemberIds(teamMemberIds);
        verify(teamMemberRepository, times(1)).findIdsByProjectIds(List.of(project2Id));
        verify(momentRepository, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNotNull_projectNotExist() {
        long creatorId = 999L;
        Long project1Id = 1L;
        List<Long> projectIds = List.of(project1Id);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(projectIds)
                .teamMemberIds(null)
                .build();

        when(projectRepository.findAllByIds(projectIds)).thenThrow(new NotFoundException(ErrorMessage.SOME_OF_PROJECTS_NOT_EXIST));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.SOME_OF_PROJECTS_NOT_EXIST.getMessage(), exception.getMessage());

        verify(projectRepository, times(1)).findAllByIds(projectIds);
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testAddNew_projectsNotNull_projectStatusInvalid() {
        long creatorId = 999L;
        Long project1Id = 1L;
        Project project1 = Project.builder()
                .id(project1Id)
                .status(ProjectStatus.COMPLETED)
                .build();
        List<Long> projectIds = List.of(project1Id);
        List<Project> projects = List.of(project1);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(projectIds)
                .teamMemberIds(null)
                .build();

        when(projectRepository.findAllByIds(projectIds)).thenReturn(projects);

        ConflictException exception = assertThrows(ConflictException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.PROJECT_STATUS_INVALID.getMessage(), exception.getMessage());

        verify(projectRepository, times(1)).findAllByIds(projectIds);
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }
}