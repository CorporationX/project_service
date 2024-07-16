package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.moment.MomentRequestDto;
import faang.school.projectservice.dto.client.moment.MomentResponseDto;
import faang.school.projectservice.dto.client.moment.MomentUpdateDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        verify(teamMemberRepository, times(2)).findIdsByProjectIds(projectIds);
        //todo: optimize
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
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(projectIds)
                .teamMemberIds(teamMemberIds)
                .build();
        Long missingTeamMember1Id = 2L;
        List<Long> teamMembersByProjects = List.of(teamMember1Id, missingTeamMember1Id);

        when(projectRepository.findAllByIds(projectIds)).thenReturn(projects);
        when(teamMemberRepository.findIdsByProjectIds(projectIds)).thenReturn(teamMembersByProjects);
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
        verify(teamMemberRepository, times(2)).findIdsByProjectIds(projectIds);
        //todo: optimize
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

    @Test
    void testUpdate_SimpleFields() {
        long userId = 999L;
        long momentId = 1L;
        String newName = "new name";
        String newDesc = "new desc";
        LocalDateTime newDate = LocalDateTime.now();
        String newImage = "new image";
        MomentUpdateDto momentUpdateDto = MomentUpdateDto.builder()
                .id(momentId)
                .name(newName)
                .description(newDesc)
                .date(newDate)
                .imageId(newImage)
                .build();
        Moment momentFromDb = Moment.builder()
                .id(momentId)
                .name("nameFromDb")
                .description("descFromDb")
                .date(LocalDateTime.now().minusDays(10))
                .imageId("imageFromDb")
                .build();

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(momentFromDb));

        MomentResponseDto updated = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, updated.getId());
        assertEquals(newName, updated.getName());
        assertEquals(newDesc, updated.getDescription());
        assertEquals(newDate, updated.getDate());
        assertEquals(newImage, updated.getImageId());
        assertEquals(userId, updated.getUpdatedBy());
        verify(momentRepository, times(1)).findById(momentId);
        verify(momentRepository, times(1)).save(Mockito.any(Moment.class));
    }

    @Test
    void testUpdate_momentNotExist() {
        long userId = 999L;
        long momentId = 1L;
        MomentUpdateDto momentUpdateDto = MomentUpdateDto.builder()
                .id(momentId)
                .build();

        when(momentRepository.findById(momentId))
                .thenThrow(new NotFoundException(ErrorMessage.MOMENT_NOT_EXIST));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> momentService.update(momentUpdateDto, userId));
        assertEquals(ErrorMessage.MOMENT_NOT_EXIST.getMessage(), exception.getMessage());
        verifyNoMoreInteractions(momentRepository, teamMemberRepository, projectRepository);
    }

    @Test
    void testUpdate_dependentFields_bothNotNull() {
        long userId = 1L;
        List<Long> newProjectIds = List.of(1L, 2L);
        List<Project> newProjects = List.of(
                Project.builder()
                        .id(1L)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .id(2L)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()
        );
        List<Long> newTeamMemberIds = List.of(11L, 21L);

        long momentId = 1L;
        MomentUpdateDto momentUpdateDto = MomentUpdateDto.builder()
                .id(momentId)
                .projectIds(newProjectIds)
                .teamMemberIds(newTeamMemberIds)
                .build();

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(
                Moment.builder()
                        .id(momentId)
                        .build()
        ));
        when(projectRepository.findAllByIds(newProjectIds)).thenReturn(newProjects);
        when(teamMemberRepository.findIdsByProjectIds(newProjectIds)).thenReturn(newTeamMemberIds);
        when(projectRepository.findAllDistinctByTeamMemberIds(newTeamMemberIds)).thenReturn(newProjects);

        MomentResponseDto responseDto = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, responseDto.getId());
        assertEquals(newProjectIds, responseDto.getProjectIds());
        assertEquals(newTeamMemberIds, responseDto.getTeamMemberIds());
        assertEquals(userId, responseDto.getUpdatedBy());
        verify(projectRepository, times(1)).findAllByIds(newProjectIds);
        verify(teamMemberRepository, times(1)).findIdsByProjectIds(newProjectIds);
        verify(projectRepository, times(1)).findAllDistinctByTeamMemberIds(newTeamMemberIds);
        verify(momentRepository, times(1)).save(any(Moment.class));
    }

    @Test
    void testUpdate_dependentFields_projectsNotNullMembersNull() {
        long userId = 1L;
        List<Long> newProjectIds = List.of(1L, 2L);
        Project project1 = Project.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project project3 = Project.builder()
                .id(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        List<Project> newProjects = List.of(project1, project2);
        List<Project> oldProjects = List.of(project1, project3);

        List<Long> oldTeamMemberIds = new ArrayList<>(List.of(11L, 31L));
        List<Long> teamMembersFromNewProjects = List.of(11L, 21L);

        long momentId = 1L;
        MomentUpdateDto momentUpdateDto = MomentUpdateDto.builder()
                .id(momentId)
                .projectIds(newProjectIds)
                .teamMemberIds(null)
                .build();

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(
                Moment.builder()
                        .id(momentId)
                        .projects(oldProjects)
                        .teamMemberIds(oldTeamMemberIds)
                        .build()
        ));
        when(projectRepository.findAllByIds(newProjectIds)).thenReturn(newProjects);
        when(teamMemberRepository.findIdsByProjectIds(newProjectIds)).thenReturn(teamMembersFromNewProjects);

        MomentResponseDto responseDto = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, responseDto.getId());
        assertEquals(newProjectIds, responseDto.getProjectIds());
        assertEquals(teamMembersFromNewProjects, responseDto.getTeamMemberIds());
        assertEquals(userId, responseDto.getUpdatedBy());
        verify(projectRepository, times(1)).findAllByIds(newProjectIds);
        verify(teamMemberRepository, times(2)).findIdsByProjectIds(newProjectIds);
        verify(momentRepository, times(1)).save(any(Moment.class));
    }

    @Test
    void testUpdate_dependentFields_projectsNullMembersNotNull() {
        long userId = 1L;
        List<Long> newTeamMemberIds = List.of(11L, 21L);
        List<Long> oldTeamMemberIds = List.of(11L, 31L);

        Project project1 = Project.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project project3 = Project.builder()
                .id(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        List<Project> oldProjects = new ArrayList<>(List.of(project1, project3));
        List<Project> projectsFromNewTeamMembers = List.of(project1, project2);
        List<Long> projectIdsFromNewTeamMembers = List.of(1L, 2L);

        long momentId = 1L;
        MomentUpdateDto momentUpdateDto = MomentUpdateDto.builder()
                .id(momentId)
                .projectIds(null)
                .teamMemberIds(newTeamMemberIds)
                .build();

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(
                Moment.builder()
                        .id(momentId)
                        .projects(oldProjects)
                        .teamMemberIds(oldTeamMemberIds)
                        .build()
        ));
        when(projectRepository.findAllDistinctByTeamMemberIds(newTeamMemberIds)).thenReturn(projectsFromNewTeamMembers);


        MomentResponseDto responseDto = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, responseDto.getId());
        assertEquals(projectIdsFromNewTeamMembers, responseDto.getProjectIds());
        assertEquals(newTeamMemberIds, responseDto.getTeamMemberIds());
        assertEquals(userId, responseDto.getUpdatedBy());
        verify(teamMemberRepository, times(1)).checkExistAll(newTeamMemberIds);
        verify(projectRepository, times(2)).findAllDistinctByTeamMemberIds(newTeamMemberIds);
        verify(momentRepository, times(1)).save(any(Moment.class));
    }
}