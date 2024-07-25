package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.utilservice.MomentUtilService;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import faang.school.projectservice.service.utilservice.TeamMemberUtilService;
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
    TeamMemberUtilService teamMemberUtilService;
    @Mock
    ProjectUtilService projectUtilService;
    @Mock
    MomentUtilService momentUtilService;
    @Spy
    MomentMapperImpl momentMapper;

    @InjectMocks
    MomentService momentService;

    @Test
    void testGetById() {
        long momentId = 1L;
        Moment momentFromDB = Moment.builder()
                .id(momentId)
                .name("nameFromDb")
                .description("descFromDb")
                .date(LocalDateTime.now())
                .projects(List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build()
                ))
                .teamMemberIds(List.of(11L, 21L))
                .imageId("imageFromDb")
                .createdAt(LocalDateTime.now())
                .createdBy(111L)
                .updatedAt(LocalDateTime.now())
                .updatedBy(111L)
                .build();

        when(momentUtilService.getById(momentId)).thenReturn(momentFromDB);

        MomentResponseDto responseDto = momentService.getById(momentId);

        assertEquals(momentFromDB.getId(), responseDto.getId());
        assertEquals(momentFromDB.getName(), responseDto.getName());
        assertEquals(momentFromDB.getDescription(), responseDto.getDescription());
        assertEquals(momentFromDB.getDate(), responseDto.getDate());
        assertEquals(
                momentFromDB.getProjects().stream().map(Project::getId).toList(),
                responseDto.getProjectIds()
        );
        assertEquals(momentFromDB.getTeamMemberIds(), responseDto.getTeamMemberIds());
        assertEquals(momentFromDB.getImageId(), responseDto.getImageId());
        assertEquals(momentFromDB.getCreatedAt(), responseDto.getCreatedAt());
        assertEquals(momentFromDB.getCreatedBy(), responseDto.getCreatedBy());
        assertEquals(momentFromDB.getUpdatedAt(), responseDto.getUpdatedAt());
        assertEquals(momentFromDB.getUpdatedBy(), responseDto.getUpdatedBy());
        verify(momentUtilService, times(1)).getById(momentId);
    }

    @Test
    void testGetAll() {
        List<Moment> moments = List.of(
                Moment.builder()
                        .id(1L)
                        .build(),
                Moment.builder()
                        .id(2L)
                        .build()
        );

        when(momentUtilService.getAll()).thenReturn(moments);

        List<MomentResponseDto> responseDtos = momentService.getAll();

        assertEquals(moments.size(), responseDtos.size());
        assertEquals(moments.get(0).getId(), responseDtos.get(0).getId());
        assertEquals(moments.get(1).getId(), responseDtos.get(1).getId());
        verify(momentUtilService, times(1)).getAll();
    }

    @Test
    void testGetAllFilteredByProjectId_filterNotNull() {
        long projectId = 1L;
        MomentFilterDto filterDto = MomentFilterDto.builder()
                .partnerProjectIds(List.of(2L, 3L))
                .build();

        List<Moment> moments = List.of(
                Moment.builder()
                        .id(1L)
                        .projects(
                                List.of(
                                        Project.builder()
                                                .id(projectId)
                                                .build(),
                                        Project.builder()
                                                .id(2L)
                                                .build()))
                        .build(),
                Moment.builder()
                        .id(2L)
                        .projects(
                                List.of(
                                        Project.builder()
                                                .id(projectId)
                                                .build(),
                                        Project.builder()
                                                .id(3L)
                                                .build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .projects(
                                List.of(
                                        Project.builder()
                                                .id(projectId)
                                                .build(),
                                        Project.builder()
                                                .id(4L)
                                                .build()))
                        .build()
        );

        when(momentUtilService.findAllByProjectIdAndDateBetween(
                projectId, filterDto.getStart(), filterDto.getEndExclusive()))
                .thenReturn(moments);

        List<MomentResponseDto> responseDtos = momentService.getAllFilteredByProjectId(projectId, filterDto);

        assertEquals(2, responseDtos.size());
        assertTrue(responseDtos.get(0).getProjectIds().contains(projectId));
        assertTrue(responseDtos.get(0).getProjectIds().contains(2L));
        assertTrue(responseDtos.get(1).getProjectIds().contains(projectId));
        assertTrue(responseDtos.get(1).getProjectIds().contains(3L));
        verify(momentUtilService, times(1))
                .findAllByProjectIdAndDateBetween(projectId, filterDto.getStart(), filterDto.getEndExclusive());
    }

    @Test
    void testGetAllFilteredByProjectId_filterNull() {
        long projectId = 1L;

        List<Moment> moments = List.of(
                Moment.builder()
                        .id(1L)
                        .projects(
                                List.of(
                                        Project.builder()
                                                .id(projectId)
                                                .build(),
                                        Project.builder()
                                                .id(2L)
                                                .build()))
                        .build(),
                Moment.builder()
                        .id(2L)
                        .projects(
                                List.of(
                                        Project.builder()
                                                .id(projectId)
                                                .build(),
                                        Project.builder()
                                                .id(3L)
                                                .build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .projects(
                                List.of(
                                        Project.builder()
                                                .id(projectId)
                                                .build(),
                                        Project.builder()
                                                .id(4L)
                                                .build()))
                        .build()
        );

        when(momentUtilService.findAllByProjectId(projectId)).thenReturn(moments);

        List<MomentResponseDto> responseDtos = momentService.getAllFilteredByProjectId(projectId, null);

        assertEquals(3, responseDtos.size());
        assertTrue(responseDtos.get(0).getProjectIds().contains(projectId));
        assertTrue(responseDtos.get(1).getProjectIds().contains(projectId));
        assertTrue(responseDtos.get(2).getProjectIds().contains(projectId));
        verify(momentUtilService, times(1))
                .findAllByProjectId(projectId);
    }

    @Test
    void testGetById_NotExists() {
        long momentId = 1L;

        when(momentUtilService.getById(momentId)).thenThrow(new NotFoundException(ErrorMessage.MOMENT_NOT_EXIST));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> momentService.getById(momentId));
        assertEquals(ErrorMessage.MOMENT_NOT_EXIST.getMessage(), exception.getMessage());

        verify(momentUtilService, times(1)).getById(momentId);
    }


    @Test
    void testAddNew_projectsNullMembersNotNull() {
        Long creatorId = 999L;
        Long teamMember1Id = 1L;
        Long teamMember1ProjectId = 11L;
        Project teamMember1Project = Project.builder()
                .id(teamMember1ProjectId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        List<Project> projectsFromTeamMembers = List.of(teamMember1Project);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(null)
                .teamMemberIds(List.of(teamMember1Id))
                .build();

        when(projectUtilService.findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds()))
                .thenReturn(projectsFromTeamMembers);
        when(momentUtilService.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getTeamMemberIds().contains(teamMember1Id));
        assertTrue(momentResponseDto.getProjectIds().contains(teamMember1ProjectId));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(teamMemberUtilService, times(1))
                .checkExistAllByIds(momentRequestDto.getTeamMemberIds());
        verify(projectUtilService, times(1))
                .findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds());
        verify(projectUtilService, times(1)).checkProjectsNotClosed(projectsFromTeamMembers);
        verify(momentUtilService, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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
                .when(teamMemberUtilService).checkExistAllByIds(momentRequestDto.getTeamMemberIds());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.SOME_OF_MEMBERS_NOT_EXIST.getMessage(), exception.getMessage());

        verify(teamMemberUtilService, times(1))
                .checkExistAllByIds(momentRequestDto.getTeamMemberIds());
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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
        List<Project> projectsFromTeamMembers = List.of(teamMember1Project);
        MomentRequestDto momentRequestDto = MomentRequestDto.builder()
                .projectIds(null)
                .teamMemberIds(List.of(teamMember1Id))
                .build();


        when(projectUtilService.findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds()))
                .thenReturn(projectsFromTeamMembers);
        doThrow(new ConflictException(ErrorMessage.PROJECT_STATUS_INVALID))
                .when(projectUtilService).checkProjectsNotClosed(projectsFromTeamMembers);

        ConflictException exception = assertThrows(ConflictException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.PROJECT_STATUS_INVALID.getMessage(), exception.getMessage());

        verify(teamMemberUtilService, times(1))
                .checkExistAllByIds(momentRequestDto.getTeamMemberIds());
        verify(projectUtilService, times(1))
                .findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds());
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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

        when(projectUtilService.getAllByIdsStrictly(projectIds)).thenReturn(projects);
        when(teamMemberUtilService.findIdsByProjectIds(projectIds)).thenReturn(project1TeamMemberIds);
        when(momentUtilService.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getProjectIds().contains(project1Id));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(project1TeamMember1Id));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(projectUtilService, times(1)).getAllByIdsStrictly(projectIds);
        verify(projectUtilService, times(1)).checkProjectsNotClosed(projects);
        verify(teamMemberUtilService, times(1)).findIdsByProjectIds(projectIds);
        verify(momentUtilService, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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

        when(projectUtilService.getAllByIdsStrictly(projectIds)).thenReturn(projects);
        when(teamMemberUtilService.findIdsByProjectIds(projectIds)).thenReturn(teamMemberIds);
        when(momentUtilService.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getProjectIds().contains(project1Id));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(teamMember1Id));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(projectUtilService, times(1)).getAllByIdsStrictly(projectIds);
        verify(projectUtilService, times(1)).checkProjectsNotClosed(projects);
        verify(teamMemberUtilService, times(1)).checkTeamMembersFitProjects(teamMemberIds, projectIds);
        verify(teamMemberUtilService, times(1)).findIdsByProjectIds(projectIds);
        verify(momentUtilService, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
    }

    @Test
    void testAddNew_projectsNotNullMembersNotNull_MembersUnFitProjects() {
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

        when(projectUtilService.getAllByIdsStrictly(projectIds)).thenReturn(projects);
        doThrow(new ConflictException(ErrorMessage.MEMBERS_UNFIT_PROJECTS))
                .when(teamMemberUtilService).checkTeamMembersFitProjects(teamMemberIds, projectIds);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> momentService.addNew(momentRequestDto, creatorId)
        );
        assertEquals(ErrorMessage.MEMBERS_UNFIT_PROJECTS.getMessage(), exception.getMessage());

        verify(projectUtilService, times(1)).getAllByIdsStrictly(projectIds);
        verify(projectUtilService, times(1)).checkProjectsNotClosed(projects);
        verify(teamMemberUtilService, times(1)).checkTeamMembersFitProjects(teamMemberIds, projectIds);
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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

        when(projectUtilService.getAllByIdsStrictly(projectIds)).thenReturn(projects);
        when(teamMemberUtilService.findIdsByProjectIds(projectIds)).thenReturn(teamMembersByProjects);
        when(momentUtilService.save(Mockito.any(Moment.class))).thenAnswer(invocationOnMock -> {
            Moment moment = invocationOnMock.getArgument(0, Moment.class);
            moment.setId(21L);
            return moment;
        });

        MomentResponseDto momentResponseDto = momentService.addNew(momentRequestDto, creatorId);

        assertTrue(momentResponseDto.getProjectIds().containsAll(projectIds));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(teamMember1Id));
        assertTrue(momentResponseDto.getTeamMemberIds().contains(missingTeamMember1Id));
        assertEquals(creatorId, momentResponseDto.getCreatedBy());
        verify(projectUtilService, times(1)).getAllByIdsStrictly(projectIds);
        verify(projectUtilService, times(1)).checkProjectsNotClosed(projects);
        verify(teamMemberUtilService, times(1)).checkTeamMembersFitProjects(teamMemberIds, projectIds);
        verify(teamMemberUtilService, times(1)).findIdsByProjectIds(projectIds);
        verify(momentUtilService, times(1)).save(Mockito.any(Moment.class));
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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

        when(projectUtilService.getAllByIdsStrictly(projectIds)).thenThrow(new NotFoundException(ErrorMessage.SOME_OF_PROJECTS_NOT_EXIST));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.SOME_OF_PROJECTS_NOT_EXIST.getMessage(), exception.getMessage());

        verify(projectUtilService, times(1)).getAllByIdsStrictly(projectIds);
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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

        when(projectUtilService.getAllByIdsStrictly(projectIds)).thenReturn(projects);
        doThrow(new ConflictException(ErrorMessage.PROJECT_STATUS_INVALID))
                .when(projectUtilService).checkProjectsNotClosed(projects);

        ConflictException exception = assertThrows(ConflictException.class, () -> momentService.addNew(momentRequestDto, creatorId));
        assertEquals(ErrorMessage.PROJECT_STATUS_INVALID.getMessage(), exception.getMessage());

        verify(projectUtilService, times(1)).getAllByIdsStrictly(projectIds);
        verify(projectUtilService, times(1)).checkProjectsNotClosed(projects);
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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

        when(momentUtilService.getById(momentId)).thenReturn(momentFromDb);

        MomentResponseDto updated = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, updated.getId());
        assertEquals(newName, updated.getName());
        assertEquals(newDesc, updated.getDescription());
        assertEquals(newDate, updated.getDate());
        assertEquals(newImage, updated.getImageId());
        assertEquals(userId, updated.getUpdatedBy());
        verify(momentUtilService, times(1)).getById(momentId);
        verify(momentUtilService, times(1)).save(Mockito.any(Moment.class));
    }

    @Test
    void testUpdate_momentNotExist() {
        long userId = 999L;
        long momentId = 1L;
        MomentUpdateDto momentUpdateDto = MomentUpdateDto.builder()
                .id(momentId)
                .build();

        when(momentUtilService.getById(momentId))
                .thenThrow(new NotFoundException(ErrorMessage.MOMENT_NOT_EXIST));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> momentService.update(momentUpdateDto, userId));
        assertEquals(ErrorMessage.MOMENT_NOT_EXIST.getMessage(), exception.getMessage());
        verifyNoMoreInteractions(momentUtilService, teamMemberUtilService, projectUtilService);
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

        when(momentUtilService.getById(momentId)).thenReturn(
                Moment.builder()
                        .id(momentId)
                        .build()
        );
        when(projectUtilService.getAllByIdsStrictly(newProjectIds)).thenReturn(newProjects);

        MomentResponseDto responseDto = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, responseDto.getId());
        assertEquals(newProjectIds, responseDto.getProjectIds());
        assertEquals(newTeamMemberIds, responseDto.getTeamMemberIds());
        assertEquals(userId, responseDto.getUpdatedBy());
        verify(momentUtilService, times(1)).getById(momentId);
        verify(projectUtilService, times(1)).getAllByIdsStrictly(newProjectIds);
        verify(projectUtilService, times(1)).checkProjectsNotClosed(newProjects);
        verify(projectUtilService, times(1)).checkProjectsFitTeamMembers(newProjectIds, newTeamMemberIds);
        verify(teamMemberUtilService, times(1)).checkTeamMembersFitProjects(newTeamMemberIds, newProjectIds);
        verify(momentUtilService, times(1)).save(any(Moment.class));
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

        when(momentUtilService.getById(momentId)).thenReturn(
                Moment.builder()
                        .id(momentId)
                        .projects(oldProjects)
                        .teamMemberIds(oldTeamMemberIds)
                        .build()
        );
        when(projectUtilService.getAllByIdsStrictly(newProjectIds)).thenReturn(newProjects);
        when(teamMemberUtilService.findIdsByProjectIds(newProjectIds)).thenReturn(teamMembersFromNewProjects);

        MomentResponseDto responseDto = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, responseDto.getId());
        assertEquals(newProjectIds, responseDto.getProjectIds());
        assertEquals(teamMembersFromNewProjects, responseDto.getTeamMemberIds());
        assertEquals(userId, responseDto.getUpdatedBy());
        verify(projectUtilService, times(1)).getAllByIdsStrictly(newProjectIds);
        verify(teamMemberUtilService, times(2)).findIdsByProjectIds(newProjectIds);
        verify(momentUtilService, times(1)).save(any(Moment.class));
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

        when(momentUtilService.getById(momentId)).thenReturn(
                Moment.builder()
                        .id(momentId)
                        .projects(oldProjects)
                        .teamMemberIds(oldTeamMemberIds)
                        .build()
        );
        when(projectUtilService.findAllDistinctByTeamMemberIds(newTeamMemberIds)).thenReturn(projectsFromNewTeamMembers);


        MomentResponseDto responseDto = momentService.update(momentUpdateDto, userId);

        assertEquals(momentId, responseDto.getId());
        assertEquals(projectIdsFromNewTeamMembers, responseDto.getProjectIds());
        assertEquals(newTeamMemberIds, responseDto.getTeamMemberIds());
        assertEquals(userId, responseDto.getUpdatedBy());
        verify(teamMemberUtilService, times(1)).checkExistAllByIds(newTeamMemberIds);
        verify(projectUtilService, times(2)).findAllDistinctByTeamMemberIds(newTeamMemberIds);
        verify(momentUtilService, times(1)).save(any(Moment.class));
    }
}