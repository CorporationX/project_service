package faang.school.projectservice.service.stageroles;

import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRolesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageRolesServiceTest {

    private static final List<Long> IDS = new ArrayList<>();
    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    @InjectMocks
    private StageRolesService stageRolesService;
    @Mock
    private StageRolesRepository stageRolesRepository;
    private StageRoles stageRolesOne;
    private StageRoles stageRolesTwo;


    @Nested
    class PositiveTest {

        @BeforeEach
        void init() {
            IDS.add(ID_ONE);
            IDS.add(ID_TWO);

            stageRolesOne = StageRoles.builder()
                    .id(ID_ONE)
                    .build();

            stageRolesTwo = StageRoles.builder()
                    .id(ID_TWO)
                    .build();
        }

        @Test
        @DisplayName("Returning a list of all users by ID")
        void whenAppealDbThenReturnAllById() {
            List<StageRoles> stageRoles = new ArrayList<>(Arrays.asList(stageRolesOne, stageRolesTwo));

            when(stageRolesRepository.findAllById(IDS)).thenReturn(stageRoles);

            List<StageRoles> result = stageRolesService.getAllById(IDS);

            assertNotNull(result);
            assertEquals(result, stageRoles);

            verify(stageRolesRepository).findAllById(IDS);
        }
    }
}