package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.util.converter.GigabyteConverter;
import faang.school.projectservice.util.converter.MultiPartFileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {
    @InjectMocks
    private ResourceValidator resourceValidator;
    @Mock
    private GigabyteConverter gigabyteConverter;
    private static final long FILE_SIZE = 1_000_000_000L;
    private static final BigInteger STORAGE_SIZE = new BigInteger(String.valueOf(Math.round(Math.pow(1000, 3)) * 2));
    private static final BigInteger STORAGE_TEST_SIZE = new BigInteger(String.valueOf(10_000L));
    private static final long FILE_TEST_SIZE = 100_000L;
    private static final long PROJECT_ID_ONE = 1L;
    private static final long PROJECT_ID_TWO = 2L;
    private static final long TEAM_MEMBER_ID_ONE = 1L;
    private TeamMember teamMember;
    private Project project;
    private MultiPartFileConverter file;

    @BeforeEach
    void setUp() {
        teamMember = TeamMember.builder()
                .id(TEAM_MEMBER_ID_ONE)
                .team(Team.builder()
                        .project(Project.builder()
                                .id(PROJECT_ID_ONE)
                                .storageSize(null)
                                .build())
                        .build())
                .build();

        project = Project.builder()
                .id(PROJECT_ID_ONE)
                .build();

        byte[] input = new byte[(int) FILE_TEST_SIZE];

        file = MultiPartFileConverter.builder()
                .input(input)
                .build();
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("When TeamMember belongs to project does not throw exception")
        public void whenTeamMemberBelongsToProjectThenDoesNoThrowException() {
            assertDoesNotThrow(() -> resourceValidator)
                    .validateTeamMemberBelongsToProject(teamMember, project.getId());
        }

        @Test
        @DisplayName("When file size less then project's storage size then doesn't throw exception")
        public void whenFileSizeFitsInProjectStorageThenDoesNotThrowException() {
            project.setStorageSize(STORAGE_SIZE);

            assertDoesNotThrow(() -> resourceValidator)
                    .validateStorageCapacity(file, project);
        }

        @Test
        @DisplayName("When file size does not fit in project's storage then throw exception")
        public void whenFileSizeMoreDoesNotFitInProjectStorageThenThrowException() {
            project.setStorageSize(STORAGE_TEST_SIZE);

            assertThrows(DataValidationException.class, () ->
                    resourceValidator.validateStorageCapacity(file, project));
        }

        @Test
        @DisplayName("When TeamMember does not belongs to project throw exception")
        public void whenTeamMemberDoesNotBelongsToProjectThenThrowException() {
            teamMember.getTeam().setProject(Project.builder().id(PROJECT_ID_TWO).build());
            assertThrows(ForbiddenException.class, () -> resourceValidator
                    .validateTeamMemberBelongsToProject(teamMember, project.getId()));
        }
//        @Test
//        @DisplayName("When project's storage size is null set it to 2 billion bytes")
//        public void whenProjectStorageSizeIsNullThenSetItForTwoBillionBytes() {
//            resourceValidator.setNewProjectStorageSize(project);
//            assertEquals(STORAGE_SIZE, project.getStorageSize());
//        }
//
//        @Test
//        @DisplayName("When size passed return representation in GBs")
//        public void whenSizePassedThenReturnGigabyteRepresentation() {
//            long gigabyteResult = resourceValidator.byteToGigabyteConverter(FILE_SIZE);
//            assertEquals(gigabyteResult, 1.0);
//        }
    }
}
