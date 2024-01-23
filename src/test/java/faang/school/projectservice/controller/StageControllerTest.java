package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.TeamRole.ANALYST;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {

    @Mock
    private StageService stageService;
    @Mock
    private StageValidator stageValidator;

    @InjectMocks
    private StageController stageController;


    @Test
    void testSave_whenInputStageIsCorrect_thenSaveToBd() {
        // Arrange
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

        // Act

        StageDto result = stageController.save(stageDto);

        // Assert

        verify(stageService, Mockito.times(1)).save(stageDto);
    }
}