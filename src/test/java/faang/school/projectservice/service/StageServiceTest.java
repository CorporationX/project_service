package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.util.StageDataUtilTest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @InjectMocks
    private StageServiceImpl stageService;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private StageMapper stageMapper;
    @Mock
    private StageRolesMapper stageRolesMapper;
    private StageDataUtilTest stageDataUtilTest = new StageDataUtilTest();


    @Test
    public void testCreateStageProjectInValid() {

        when(projectRepository.findById(stageDataUtilTest.getStageDto().getProjectId()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> stageService.createStage(stageDataUtilTest.getStageDto()));
        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(0))
                .save(any());
    }

    @Test
    public void testCreateStageProjectStatusInValid() {
        Project project = stageDataUtilTest.getProject();
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.findById(stageDataUtilTest.getStageDto().getProjectId()))
                .thenReturn(Optional.ofNullable(project));

        assertThrows(DataValidationException.class,
                () -> stageService.createStage(stageDataUtilTest.getStageDto()));

        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(0))
                .save(any());
    }

    @Test
    public void testCreateStageProjectNotExistsByOwnerIdInValid() {
        Project project = stageDataUtilTest.getProject();

        when(projectRepository.findById(stageDataUtilTest.getStageDto().getProjectId()))
                .thenReturn(Optional.ofNullable(project));
        when(projectRepository.existsByOwnerIdAndName(anyLong(), anyString()))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> stageService.createStage(stageDataUtilTest.getStageDto()));

        Mockito.verify(projectRepository, Mockito.times(1))
                .existsByOwnerIdAndName(anyLong(), anyString());
        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(0))
                .save(any());
    }


    @Test
    public void testCreateStageValid() {

        when(projectRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(stageDataUtilTest.getProject()));
        when(projectRepository.existsByOwnerIdAndName(anyLong(), anyString()))
                .thenReturn(true);
        when(stageMapper.toEntity(stageDataUtilTest.getStageDto()))
                .thenReturn(stageDataUtilTest.getStage());
        when(stageRolesMapper.toEntity(stageDataUtilTest.getStageDto().getStageRoles().get(0)))
                .thenReturn(stageDataUtilTest.getStageRoles());

        stageService.createStage(stageDataUtilTest.getStageDto());

        Mockito.verify(projectRepository, Mockito.times(1))
                .existsByOwnerIdAndName(anyLong(), anyString());
        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(1))
                .save(any());
    }

    @Test
    public void testGetAllStagesByFilterValid() {

        when(stageRepository.findAll())
                .thenReturn(List.of(stageDataUtilTest.getStage()));
        when(stageMapper.toDto(stageDataUtilTest.getStage()))
                .thenReturn(stageDataUtilTest.getStageDto());

        StageFilterDto filter = stageDataUtilTest.getStageFilterDto();
        List<StageDto> stages = stageService.getAllStagesByFilter(filter);

        assert (stages.size() == 1);

        Mockito.verify(stageRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    public void testGetAllStagesByRoleFilterValid() {

        when(stageRepository.findAll())
                .thenReturn(List.of(stageDataUtilTest.getStage()));
        when(stageMapper.toDto(stageDataUtilTest.getStage()))
                .thenReturn(stageDataUtilTest.getStageDto());

        StageFilterDto filter = stageDataUtilTest.getStageFilterDto();
        filter.setStatus("");
        List<StageDto> stages = stageService.getAllStagesByFilter(filter);

        assert (stages.size() == 1);

        Mockito.verify(stageRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    public void testGetAllStagesByStatusFilterValid() {

        when(stageRepository.findAll())
                .thenReturn(List.of(stageDataUtilTest.getStage()));
        when(stageMapper.toDto(stageDataUtilTest.getStage()))
                .thenReturn(stageDataUtilTest.getStageDto());

        StageFilterDto filter = stageDataUtilTest.getStageFilterDto();
        filter.setRole("");
        List<StageDto> stages = stageService.getAllStagesByFilter(filter);

        assert (stages.size() == 1);

        Mockito.verify(stageRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    public void testGetAllStagesFilterReturnEmptyValid() {

        when(stageRepository.findAll())
                .thenReturn(List.of(stageDataUtilTest.getStage()));

        StageFilterDto filter = stageDataUtilTest.getStageFilterDto();
        filter.setRole("DEVELOPER");
        List<StageDto> stages = stageService.getAllStagesByFilter(filter);

        assert (stages.size() == 0);

        Mockito.verify(stageRepository, Mockito.times(1))
                .findAll();
    }

}

