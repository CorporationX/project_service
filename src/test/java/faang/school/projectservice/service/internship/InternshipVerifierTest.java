package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.testData.internship.InternshipTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InternshipVerifierTest {
    private InternshipVerifier internshipVerifier;
    private Internship internship;
    private InternshipDto internshipDto;


    @BeforeEach
    void setUp() {
        internshipVerifier = new InternshipVerifier();

        InternshipTestData internshipTestData = new InternshipTestData();
        internship = internshipTestData.getInternship();
        internshipDto = internshipTestData.getInternshipDto();
    }

    @Nested
    class PositiveTests {
        @DisplayName("shouldn't throw exception when all interns from dto exists")
        @Test
        void verifyExistenceOfAllInternsTest() {
            assertDoesNotThrow(() -> internshipVerifier.verifyExistenceOfAllInterns(internship.getInterns(),
                    internshipDto.getInternsIds().size()));
        }

        @DisplayName("shouldn't throw exception when mentors project equals to internship project")
        @Test
        void verifyMentorsProjectTest() {
            assertDoesNotThrow(() -> internshipVerifier.verifyMentorsProject(internship.getProject(),
                    internship.getMentorId()));
        }

        @DisplayName("shouldn't throw exception when updated interns are contained in internship before update")
        @Test
        void verifyUpdatedInternsTest() {
            List<TeamMember> internsAfterUpdate = new ArrayList<>(internship.getInterns());

            assertDoesNotThrow(() -> internshipVerifier.verifyUpdatedInterns(internship,
                    internsAfterUpdate));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when some of interns from dto doesn't exist")
        @Test
        void verifyExistenceOfAllInternsTest() {
            internshipDto.setInternsIds(List.of(1L, 2L, 3L, 6L));

            assertThrows(DataValidationException.class,
                    () -> internshipVerifier.verifyExistenceOfAllInterns(internship.getInterns(),
                            internshipDto.getInternsIds().size()));
        }

        @DisplayName("should throw exception when mentors project doesn't equal to internship project")
        @Test
        void verifyMentorsProjectTest() {
            internship.setProject(new Project());

            assertThrows(DataValidationException.class,
                    () -> internshipVerifier.verifyMentorsProject(internship.getProject(),
                            internship.getMentorId()));
        }

        @DisplayName("should throw exception when some of updated interns aren't contained in internship before update")
        @Test
        void verifyUpdatedInternsTest() {
            List<TeamMember> internsAfterUpdate = new ArrayList<>(internship.getInterns());
            internsAfterUpdate.add(new TeamMember());

            assertThrows(DataValidationException.class, () -> internshipVerifier.verifyUpdatedInterns(internship,
                    internsAfterUpdate));
        }
    }
}