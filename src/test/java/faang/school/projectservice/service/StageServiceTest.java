package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.StageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.TeamRole.ANALYST;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageMapper stageMapper;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private StageValidator stageValidator;

    @InjectMocks
    private StageService stageService;


    @Test
    void testSave() {
        //Arrange
        List<StageRolesDto> stageRolesDtos = List.of(
                StageRolesDto.builder()
                        .id(10L).teamRole(ANALYST)
                        .count(3)
                        .stageId(3L).build());

        StageDto stageDto = StageDto.builder()
                .stageId(3L)
                .stageName("Explore")
                .projectId(1L)
                .stageRolesDto(stageRolesDtos).build();

        Stage stage = Stage.builder()
                .stageId(stageDto.getStageId())
                .project(Project.builder().id(stageDto.getProjectId()).build())
                .stageName(stageDto.getStageName())
//                .stageRoles(stageDto.getStageRolesDto())
                .build();

        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
//        when(stageRepository.save(stage)).thenReturn(stage);

        //Act
        StageDto result = stageService.save(stageDto);

        //Assert
        assertAll(
                () -> verify(stageRepository, times(1)).save(stage)
//                ()-> assertEquals(result, stageDto)
        );
    }
}