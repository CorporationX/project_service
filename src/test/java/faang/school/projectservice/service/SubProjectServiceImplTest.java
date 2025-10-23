package faang.school.projectservice.service;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ScheduleRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.subproject.SubProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceImplTest {
    @InjectMocks
    private SubProjectServiceImpl subProjectServiceImpl;

    @Spy
    private SubProjectMapper subProjectMapper = Mappers.getMapper(SubProjectMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Captor
    private ArgumentCaptor<Project> subProjectCaptor;

    private long anyLong;
    private long differentAnyLong;

    @BeforeEach
    public void setUp() {
        anyLong = 1L;
        differentAnyLong = 2L;
    }

    @Test
    public void createFromAlienProject() {
        long creatorId = anyLong;
        CreateSubProjectDto createSubProjectDto = createCreateSubProjectDtoForTest();
        Project parentProject = new Project();
        parentProject.setOwnerId(differentAnyLong);
        when(projectRepository.findById(createSubProjectDto.parentProjectId())).thenReturn(Optional.of(parentProject));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.create(creatorId, createSubProjectDto));
    }

    @Test
    public void createPublicSubProjectFromPrivateProject() {
        long creatorId = anyLong;
        CreateSubProjectDto createSubProjectDto = createCreateSubProjectDtoForTest();
        Project parentProject = new Project();
        parentProject.setOwnerId(anyLong);
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.findById(createSubProjectDto.parentProjectId())).thenReturn(Optional.of(parentProject));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.create(creatorId, createSubProjectDto));
    }

    @Test
    public void createCreates() {
        long creatorId = anyLong;
        CreateSubProjectDto createSubProjectDto = createCreateSubProjectDtoForTest();
        Project parentProject = new Project();
        parentProject.setOwnerId(anyLong);
        parentProject.setId(differentAnyLong);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        Project subProjectToCreate = new Project();
        subProjectToCreate.setId(differentAnyLong);
        subProjectToCreate.setName("1");

        when(projectRepository.findById(createSubProjectDto.parentProjectId())).thenReturn(Optional.of(parentProject));
        when(teamRepository.findAllById(createSubProjectDto.teamIds())).thenReturn(List.of(new Team()));
        when(stageRepository.findAllById(createSubProjectDto.stageIds())).thenReturn(List.of(new Stage()));
        when(vacancyRepository.findAllById(createSubProjectDto.vacancyIds())).thenReturn(List.of(new Vacancy()));
        when(meetRepository.findAllById(createSubProjectDto.meetIds())).thenReturn(List.of(new Meet()));
        when(scheduleRepository.findById(createSubProjectDto.scheduleId())).thenReturn(Optional.of(new Schedule()));

        subProjectServiceImpl.create(creatorId, createSubProjectDto);

        verify(projectRepository, times(1)).save(subProjectCaptor.capture());
        assertEquals(subProjectToCreate.getName(), subProjectCaptor.getValue().getName());
    }

    @Test
    public void updateByNotOwner() {
        long requesterId = 1L;
        UpdateSubProjectDto updateSubProjectDto = createUpdateSubProjectDtoForTest();
        Project subProjectToUpdate = new Project();
        subProjectToUpdate.setOwnerId(2L);

        when(projectRepository.findById(updateSubProjectDto.id())).thenReturn(Optional.of(subProjectToUpdate));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.update(requesterId, updateSubProjectDto));
    }

    @Test
    public void updateMakePublicSubProjectFromPrivateProject() {
        long requesterId = 1L;
        UpdateSubProjectDto updateSubProjectDto = createUpdateSubProjectDtoForTest();
        Project subProjectToUpdate = new Project();
        subProjectToUpdate.setOwnerId(1L);
        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        subProjectToUpdate.setParentProject(parentProject);

        when(projectRepository.findById(updateSubProjectDto.id())).thenReturn(Optional.of(subProjectToUpdate));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.update(requesterId, updateSubProjectDto));
    }

    @Test
    public void updateMakeChildrenPrivate() {
        long requesterId = 1L;
        UpdateSubProjectDto updateSubProjectDto = createPrivateUpdateSubProjectDtoForTest();
        Project subProjectToUpdate = new Project();
        subProjectToUpdate.setOwnerId(1L);
        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        subProjectToUpdate.setParentProject(parentProject);
        Project childSubProject = new Project();
        childSubProject.setVisibility(ProjectVisibility.PUBLIC);
        subProjectToUpdate.setChildren(List.of(childSubProject));

        when(projectRepository.findById(updateSubProjectDto.id())).thenReturn(Optional.of(subProjectToUpdate));
        when(teamRepository.findAllById(updateSubProjectDto.teamIds())).thenReturn(List.of(new Team()));
        when(stageRepository.findAllById(updateSubProjectDto.stageIds())).thenReturn(List.of(new Stage()));
        when(vacancyRepository.findAllById(updateSubProjectDto.vacancyIds())).thenReturn(List.of(new Vacancy()));
        when(meetRepository.findAllById(updateSubProjectDto.meetIds())).thenReturn(List.of(new Meet()));
        when(scheduleRepository.findById(updateSubProjectDto.scheduleId())).thenReturn(Optional.of(new Schedule()));

        subProjectServiceImpl.update(requesterId, updateSubProjectDto);

        assertEquals(ProjectVisibility.PRIVATE, childSubProject.getVisibility());
    }

    @Test
    public void updateUpdatesSubproject() {
        long requesterId = 1L;
        UpdateSubProjectDto updateSubProjectDto = createPrivateUpdateSubProjectDtoForTest();
        Project subProjectToUpdate = new Project();
        subProjectToUpdate.setOwnerId(1L);
        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        subProjectToUpdate.setParentProject(parentProject);
        subProjectToUpdate.setChildren(new ArrayList<>());

        when(projectRepository.findById(updateSubProjectDto.id())).thenReturn(Optional.of(subProjectToUpdate));
        when(teamRepository.findAllById(updateSubProjectDto.teamIds())).thenReturn(List.of(new Team()));
        when(stageRepository.findAllById(updateSubProjectDto.stageIds())).thenReturn(List.of(new Stage()));
        when(vacancyRepository.findAllById(updateSubProjectDto.vacancyIds())).thenReturn(List.of(new Vacancy()));
        when(meetRepository.findAllById(updateSubProjectDto.meetIds())).thenReturn(List.of(new Meet()));
        when(scheduleRepository.findById(updateSubProjectDto.scheduleId())).thenReturn(Optional.of(new Schedule()));

        subProjectServiceImpl.update(requesterId, updateSubProjectDto);

        verify(projectRepository, times(1)).save(subProjectCaptor.capture());
        assertEquals(updateSubProjectDto.name(), subProjectCaptor.getValue().getName());
    }

    @Test
    public void completeWithNotCompletedChildren() {
        long requesterId = 1L;
        long subprojectId = 2L;
        Project subProjectToComplete = new Project();
        subProjectToComplete.setStatus(ProjectStatus.COMPLETED);
        Project childSubProject = new Project();
        childSubProject.setStatus(ProjectStatus.IN_PROGRESS);
        subProjectToComplete.setChildren(List.of(childSubProject));

        when(projectRepository.findById(subprojectId)).thenReturn(Optional.of(subProjectToComplete));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.complete(requesterId, subprojectId));
    }

    @Test
    public void completeByNotOwner() {
        long requesterId = 1L;
        long subprojectId = 2L;
        Project subProjectToComplete = new Project();
        subProjectToComplete.setStatus(ProjectStatus.COMPLETED);
        subProjectToComplete.setOwnerId(999L);
        Project childSubProject = new Project();
        childSubProject.setStatus(ProjectStatus.COMPLETED);
        subProjectToComplete.setChildren(List.of(childSubProject));

        when(projectRepository.findById(subprojectId)).thenReturn(Optional.of(subProjectToComplete));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.complete(requesterId, subprojectId));
    }

    @Test
    public void completeCompletes() {
        long requesterId = 1L;
        long subprojectId = 2L;
        Project subProjectToComplete = new Project();
        subProjectToComplete.setStatus(ProjectStatus.COMPLETED);
        subProjectToComplete.setOwnerId(1L);
        Project childSubProject = new Project();
        childSubProject.setStatus(ProjectStatus.COMPLETED);
        subProjectToComplete.setChildren(List.of(childSubProject));

        when(projectRepository.findById(subprojectId)).thenReturn(Optional.of(subProjectToComplete));

        subProjectServiceImpl.complete(requesterId, subprojectId);

        assertEquals(ProjectStatus.COMPLETED, subProjectToComplete.getStatus());
    }

    @Test
    public void getByIdNonexistentSubProject() {
        long subprojectId = 1L;

        assertThrows(EntityNotFoundException.class, () -> subProjectServiceImpl.getById(subprojectId));
    }

    @Test
    public void getByIdReturnsSubproject() {
        long subprojectId = 1L;

        when(projectRepository.findById(subprojectId)).thenReturn(Optional.of(new Project()));

        subProjectServiceImpl.getById(subprojectId);

        verify(projectRepository, times(1)).findById(subprojectId);
    }

    @Test
    public void getAllByParentProjectReturnsListSubProject() {
        long parentProjectId = 1L;

        subProjectServiceImpl.getAllByParentProject(parentProjectId);

        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void deleteByNotOwner() {
        long requesterId = 1L;
        long subprojectId = 2L;
        Project subProjectToDelete = new Project();
        subProjectToDelete.setOwnerId(999L);
        when(projectRepository.findById(subprojectId)).thenReturn(Optional.of(subProjectToDelete));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.delete(requesterId, subprojectId));
    }

    @Test
    public void deleteWithExistentChildren() {
        long requesterId = 1L;
        long subprojectId = 2L;
        Project subProjectToDelete = new Project();
        subProjectToDelete.setOwnerId(1L);
        subProjectToDelete.setChildren(List.of(new Project()));
        when(projectRepository.findById(subprojectId)).thenReturn(Optional.of(subProjectToDelete));

        assertThrows(ForbiddenException.class, () -> subProjectServiceImpl.delete(requesterId, subprojectId));
    }

    @Test
    public void deleteDeletes() {
        long requesterId = 1L;
        long subprojectId = 2L;
        Project subProjectToDelete = new Project();
        subProjectToDelete.setOwnerId(1L);
        subProjectToDelete.setChildren(new ArrayList<>());
        when(projectRepository.findById(subprojectId)).thenReturn(Optional.of(subProjectToDelete));

        subProjectServiceImpl.delete(requesterId, subprojectId);

        verify(projectRepository).deleteById(subprojectId);
    }


    private CreateSubProjectDto createCreateSubProjectDtoForTest() {
        return new CreateSubProjectDto(
                "1",
                "1",
                anyLong,
                ProjectVisibility.PUBLIC,
                "coveredImageId",
                List.of(anyLong, differentAnyLong),
                anyLong,
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                "presentationFileKey",
                List.of("1", "2")
        );
    }

    private UpdateSubProjectDto createUpdateSubProjectDtoForTest() {
        return new UpdateSubProjectDto(
                anyLong,
                "1",
                "1",
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                List.of("1", "2"),
                ProjectStatus.IN_PROGRESS,
                ProjectVisibility.PUBLIC,
                "coveredImageId",
                List.of(anyLong, differentAnyLong),
                anyLong,
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                "presentationFileKey",
                LocalDateTime.of(2025, 10, 10, 15, 23),
                List.of("1", "2")
        );
    }

    private UpdateSubProjectDto createPrivateUpdateSubProjectDtoForTest() {
        return new UpdateSubProjectDto(
                anyLong,
                "1",
                "1",
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                List.of("1", "2"),
                ProjectStatus.IN_PROGRESS,
                ProjectVisibility.PRIVATE,
                "coveredImageId",
                List.of(anyLong, differentAnyLong),
                anyLong,
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                List.of(anyLong, differentAnyLong),
                "presentationFileKey",
                LocalDateTime.of(2025, 10, 10, 15, 23),
                List.of("1", "2")
        );
    }
}
