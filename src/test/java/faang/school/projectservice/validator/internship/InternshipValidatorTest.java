package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.validator.intership.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class InternshipValidatorTest {

    @InjectMocks
    private InternshipValidator internshipValidator;
    private InternshipDto internshipDto;
    private static final long PROJECT_ID_ONE = 1L;
    private static final LocalDateTime VALID_START_DATE =
            LocalDateTime.of(2024, 8, 10, 10, 0);
    private static final LocalDateTime VALID_END_DATE =
            LocalDateTime.of(2024, 11, 5, 10, 0);
    private static final LocalDateTime INVALID_START_DATE =
            LocalDateTime.of(2024, 8, 10, 10, 0);
    private static final LocalDateTime INVALID_END_DATE =
            LocalDateTime.of(2024, 11, 15, 10, 0);


    @Nested
    class PositiveTests {
        @Test
        @DisplayName("When internship has project, interns and duration is less than three months" +
                " then don't throw exception")
        public void whenInternshipIsValidThenDoesNotThrowException() {
            internshipDto = InternshipDto.builder()
                    .projectId(PROJECT_ID_ONE)
                    .interns(List.of(TeamMemberDto.builder().build()))
                    .startDate(VALID_START_DATE)
                    .endDate(VALID_END_DATE)
                    .build();

            assertDoesNotThrow(() -> internshipValidator.validateInternship(internshipDto));
        }

        @Nested
        class NegativeTests {

            @Test
            @DisplayName("If internship duration is more than three months throw exception")
            public void whenInternshipDurationIsMoreThanThreeMonthsThenThrowException() {
                internshipDto = InternshipDto.builder()
                        .interns(List.of(TeamMemberDto.builder().build()))
                        .projectId(PROJECT_ID_ONE)
                        .startDate(INVALID_START_DATE)
                        .endDate(INVALID_END_DATE)
                        .build();

                assertThrows(DataValidationException.class, () ->
                        internshipValidator.validateInternship(internshipDto));
            }

            @Test
            @DisplayName("When Project and/or Mentor is not found and null then throw exception")
            public void whenProjectOrMentorIsNotFoundThenThrowException() {
                Internship internshipWithNoProject = Internship.builder()
                        .mentorId(TeamMember.builder().build())
                        .build();
                Internship internshipWithNoMentor = Internship.builder()
                        .project(Project.builder().build())
                        .build();
                assertThrows(EntityNotFoundException.class, () ->
                        internshipValidator.validateInternshipProjectAndMentorExist(internshipWithNoProject
                                .getProject(), internshipWithNoProject.getMentorId()));
                assertThrows(EntityNotFoundException.class, () ->
                        internshipValidator.validateInternshipProjectAndMentorExist(internshipWithNoMentor.getProject(),
                                internshipWithNoMentor.getMentorId()));
            }
        }
    }
}
