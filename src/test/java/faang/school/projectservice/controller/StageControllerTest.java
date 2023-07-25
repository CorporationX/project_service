package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.mapper.stage.StageRolesMapperImpl;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

public class StageControllerTest {
    @Mock
    private StageMapper stageMapper = new StageMapperImpl();
    @Mock
    private StageRolesMapperImpl stageRolesMapper = new StageRolesMapperImpl();
    @Mock
    private StageService stageService;
    @InjectMocks
    private StageController stageController = new StageController(stageService);

    private StageRolesDto stageRolesDto;
    private StageDto stageDto;

    @BeforeEach
    void init() {
        stageDto = StageDto.builder().build();
    }

    @Test
    void testValidation_NullStageDto() {
        Assertions.assertThrows(DataValidationException.class, () -> stageController.createStage(null));
    }

    @Test
    void testValidation_NullProjectId() {
        stageDto.setProjectId(null);
        Assertions.assertThrows(DataValidationException.class, () -> stageController.createStage(stageDto));
    }

    @Test
    void testValidation_NullStageName() {
        stageDto.setProjectId(1L);
        stageDto.setStageName(null);
        Assertions.assertThrows(DataValidationException.class, () -> stageController.createStage(stageDto));
    }

    @Test
    void testValidation_BlankStageName() {
        stageDto.setProjectId(1L);
        stageDto.setStageName("  ");
        Assertions.assertThrows(DataValidationException.class, () -> stageController.createStage(stageDto));
    }

    @Test
    void testValidation_NullStageRoles() {
        stageRolesDto = StageRolesDto.builder().build();
        stageDto.setProjectId(1L);
        stageDto.setStageName("123");
        Assertions.assertThrows(DataValidationException.class, () -> stageController.createStage(stageDto));
    }

    @Test
    void testValidation_EmptyStageRoles() {
        List<StageRolesDto> stageRoles = new ArrayList<>();
        stageDto.setProjectId(1L);
        stageDto.setStageName("123");
        stageDto.setStageRoles(stageRoles);
        Assertions.assertThrows(DataValidationException.class, () -> stageController.createStage(stageDto));
    }
}