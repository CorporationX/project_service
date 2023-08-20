package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoReqCreate;
import faang.school.projectservice.exception.SubprojectException;
import faang.school.projectservice.mapper.subproject.SubprojectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.ArrayList;

import static faang.school.projectservice.commonMessage.SubprojectErrMessage.ERR_VISIBILITY_PARENT_PROJECT_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubprojectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageRepository stageRepository;

    @Spy
    private SubprojectMapperImpl subprojectMapper;

    @InjectMocks
    private SubprojectService subprojectService;

    private Project parentProject;

    private SubprojectDtoReqCreate subprojectDtoReqCreate;

    @BeforeEach
    void setUp() {
        parentProject = getParentProject();
        subprojectDtoReqCreate = getSubprojectDtoReqCreate();
    }

    @Test
    void testCreateSubproject_WhenInputDataIsValid() {
        Long existParentId = ValuesForTest.PARENT.getId();
        Mockito.when(projectRepository.getProjectById(existParentId)).thenReturn(parentProject);
        Mockito.when(projectRepository.findAllByIds(subprojectDtoReqCreate.getChildrenIds())).thenReturn(new ArrayList<>());
        Mockito.when(stageRepository.findAllByIds(subprojectDtoReqCreate.getStagesIds())).thenReturn(new ArrayList<>());
        Mockito.when(projectRepository.save(Mockito.any())).thenReturn(getSubprojectAfterSave());
        SubprojectDtoReqCreate expectedDto = getExpectedDtoAfterCreate();

        SubprojectDtoReqCreate resultDto = subprojectService.createSubproject(existParentId, subprojectDtoReqCreate);

        assertEquals(expectedDto, resultDto);
        Mockito.verify(projectRepository, Mockito.times(1)).getProjectById(existParentId);
        Mockito.verify(projectRepository, Mockito.times(1))
                .findAllByIds(subprojectDtoReqCreate.getChildrenIds());
        Mockito.verify(stageRepository, Mockito.times(1))
                .findAllByIds(subprojectDtoReqCreate.getStagesIds());
        Mockito.verify(projectRepository, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void testCreateSubproject_WhenParentVisibilityPublicAndRequiredVisibilityPrivate_shouldThrowException() {
        Long parentId = ValuesForTest.PARENT.getId();
        ProjectVisibility requiredVisibility = ProjectVisibility.PRIVATE;
        subprojectDtoReqCreate.setVisibility(requiredVisibility);
        Mockito.when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        String expectedMessage = MessageFormat.format(ERR_VISIBILITY_PARENT_PROJECT_FORMAT, requiredVisibility);

        Exception exception = assertThrows(SubprojectException.class,
                () -> subprojectService.createSubproject(parentId, subprojectDtoReqCreate));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreateSubproject_WhenParentProjectNotExist_shouldThrowException() {
        Long parentId = ValuesForTest.PARENT.getId();
        String expectedMessage = String.format("Project not found by id: %s", parentId);
        Mockito.when(projectRepository.getProjectById(parentId))
                .thenThrow(new EntityNotFoundException(String.format("Project not found by id: %s", parentId)));

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> subprojectService.createSubproject(parentId, subprojectDtoReqCreate));

        assertEquals(expectedMessage, exception.getMessage());
    }

    private Project getParentProject() {
        return Project.builder()
                .id(ValuesForTest.PARENT.getId())
                .name(ValuesForTest.PARENT.getValue())
                .children(new ArrayList<>())
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private Project getSubprojectAfterSave() {
        return Project.builder()
                .id(ValuesForTest.DTO_INFO.getId())
                .name(ValuesForTest.DTO_INFO.getValue())
                .description("Description")
                .parentProject(parentProject)
                .ownerId(5L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .stages(new ArrayList<>())
                .build();
    }

    private SubprojectDtoReqCreate getSubprojectDtoReqCreate() {
        return SubprojectDtoReqCreate.builder()
                .name(ValuesForTest.DTO_INFO.getValue())
                .description("Description")
                .ownerId(5L)
                .childrenIds(new ArrayList<>(0))
                .stagesIds(new ArrayList<>(0))
                .build();
    }

    private SubprojectDtoReqCreate getExpectedDtoAfterCreate() {
        return SubprojectDtoReqCreate.builder()
                .subprojectId(ValuesForTest.DTO_INFO.getId())
                .name(ValuesForTest.DTO_INFO.getValue())
                .description("Description")
                .ownerId(5L)
                .parentProjectId(ValuesForTest.PARENT.getId())
                .childrenIds(new ArrayList<>(0))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .stagesIds(new ArrayList<>(0))
                .build();
    }
}


enum ValuesForTest {
    PARENT(1L, "Parent name"),
    CHILD1(10L, "Child 1"),
    CHILD2(11L, "Child 2"),
    DTO_INFO(100L, "Name subproject");

    private final String value;
    private final Long id;

    ValuesForTest(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Long getId() {
        return id;
    }
}