package faang.school.projectservice.validator.stagerole;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.validator.stagerole.impl.StageRolesValidatorImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageRolesValidatorTest {

    private static final long stageRoleId1 = 1;
    private static final long stageRoleId2 = 2;
    private static final long stageRoleId3 = 3;
    private static final long stageRoleId4 = 4;
    private static final long stageRoleId5 = 5;
    private static final long stageRoleId6 = 6;
    private static final long stageRoleId7 = 7;
    private static final StageRoles stageRoleWithId1 = StageRoles.builder().id(stageRoleId1).build();
    private static final StageRoles stageRoleWithId2 = StageRoles.builder().id(stageRoleId2).build();
    private static final StageRoles stageRoleWithId3 = StageRoles.builder().id(stageRoleId3).build();
    private static final StageRoles stageRoleWithId4 = StageRoles.builder().id(stageRoleId4).build();
    private static final StageRoles stageRoleWithId5 = StageRoles.builder().id(stageRoleId5).build();
    private static final StageRoles stageRoleWithId6 = StageRoles.builder().id(stageRoleId6).build();
    private static final StageRoles stageRoleWithId7 = StageRoles.builder().id(stageRoleId7).build();
    @Mock
    private StageRolesRepository stageRolesRepository;
    @InjectMocks
    private StageRolesValidatorImpl stageRolesValidator;
    @Captor
    private ArgumentCaptor<Long> idCaptor;

    private static Stream<Arguments> provideArgumentsForTestValidateStageRolesExistenceShouldReturnStageRoles() {
        return Stream.of(
                Arguments.of(stageRoleId1, stageRoleWithId1),
                Arguments.of(stageRoleId2, stageRoleWithId2),
                Arguments.of(stageRoleId3, stageRoleWithId3),
                Arguments.of(stageRoleId4, stageRoleWithId4),
                Arguments.of(stageRoleId5, stageRoleWithId5),
                Arguments.of(stageRoleId6, stageRoleWithId6),
                Arguments.of(stageRoleId7, stageRoleWithId7)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestValidateStageRolesExistenceShouldThrowException() {
        return Stream.of(
                Arguments.of(stageRoleId1, null),
                Arguments.of(stageRoleId2, null),
                Arguments.of(stageRoleId3, null),
                Arguments.of(stageRoleId4, null),
                Arguments.of(stageRoleId5, null),
                Arguments.of(stageRoleId6, null),
                Arguments.of(stageRoleId7, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateStageRolesExistenceShouldReturnStageRoles")
    public void testValidateStageRolesExistenceShouldReturnStageRoles(long stageRoleId, StageRoles stageRoles) {
        when(stageRolesRepository.findById(stageRoleId))
                .thenReturn(Optional.of(stageRoles));

        var actualStageRoles = stageRolesValidator.validateStageRolesExistence(stageRoleId);
        verify(stageRolesRepository, times(1))
                .findById(idCaptor.capture());
        var actualStageRoleId = idCaptor.getValue();

        assertEquals(stageRoleId, actualStageRoleId);
        assertEquals(stageRoles, actualStageRoles);

        verifyNoMoreInteractions(stageRolesRepository);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateStageRolesExistenceShouldThrowException")
    public void testValidateStageRolesExistenceShouldThrowException(long stageRoleId, StageRoles stageRoles) {
        when(stageRolesRepository.findById(stageRoleId))
                .thenReturn(Optional.ofNullable(stageRoles));
        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> stageRolesValidator.validateStageRolesExistence(stageRoleId));
        verify(stageRolesRepository, times(1))
                .findById(idCaptor.capture());

        var expectedMessage = String.format("a stage role with %d does not exist", stageRoleId);
        var actualMessage = actualException.getMessage();


        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(stageRolesRepository);
    }
}
