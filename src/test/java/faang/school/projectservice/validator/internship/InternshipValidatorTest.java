package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exception.internship.InternshipValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.intership.InternshipValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class InternshipValidatorTest {

    @InjectMocks
    private InternshipValidator internshipValidator;
    @Mock
    private InternshipRepository internshipRepository;
    private InternshipDto internshipDto;

    private static final long INTERNSHIP_ID_IS_ONE = 1L;
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
        @DisplayName("If internship exists don't throw exception")
        public void whenInternshipExistsThenDoesNotThrowException() {
            when(internshipRepository.findById(INTERNSHIP_ID_IS_ONE))
                    .thenReturn(Optional.of(Internship.builder().build()));

            assertDoesNotThrow(() -> internshipValidator.validateInternshipExists(INTERNSHIP_ID_IS_ONE));
        }

        @Test
        @DisplayName("When internship has project, interns and duration is less than three months" +
                " then don't throw exception")
        public void whenInternshipIsValidThenDoesNotThrowException() {
            internshipDto = InternshipDto.builder()
                    .project(ProjectDto.builder().build())
                    .interns(List.of(TeamMemberDto.builder().build()))
                    .startDate(VALID_START_DATE)
                    .endDate(VALID_END_DATE)
                    .build();

            assertDoesNotThrow(() -> internshipValidator.validateInternship(internshipDto));
        }

        @Nested
        class NegativeTests {
            @Test
            @DisplayName("If Internship isn't in DB then throw exception")
            public void whenInternshipDoesNotExistThenThrowException() {
                when(internshipRepository.findById(INTERNSHIP_ID_IS_ONE)).thenReturn(Optional.empty());

                assertThrows(InternshipValidationException.class, () ->
                        internshipValidator.validateInternshipExists(INTERNSHIP_ID_IS_ONE));
            }

            @Test
            @DisplayName("If internship has null project field and/or empty Interns list then throw exception")
            public void whenInternshipProjectIsNullAndOrInternsListIsEmptyThenThrowException() {
                InternshipDto internshipDtoNullProject = InternshipDto.builder()
                        .interns(List.of(TeamMemberDto.builder().build()))
                        .startDate(VALID_START_DATE)
                        .endDate(VALID_END_DATE)
                        .build();
                InternshipDto internshipDtoNullList = InternshipDto.builder()
                        .project(ProjectDto.builder().build())
                        .startDate(VALID_START_DATE)
                        .endDate(VALID_END_DATE)
                        .build();
                assertThrows(InternshipValidationException.class, () ->
                        internshipValidator.validateInternship(internshipDtoNullList));
                assertThrows(InternshipValidationException.class, () ->
                        internshipValidator.validateInternship(internshipDtoNullProject));
            }

            @Test
            @DisplayName("If internship duration is more than three months throw exception")
            public void whenInternshipDurationIsMoreThanThreeMonthsThenThrowException() {
                internshipDto = InternshipDto.builder()
                        .interns(List.of(TeamMemberDto.builder().build()))
                        .project(ProjectDto.builder().build())
                        .startDate(INVALID_START_DATE)
                        .endDate(INVALID_END_DATE)
                        .build();

                assertThrows(InternshipValidationException.class, () ->
                        internshipValidator.validateInternship(internshipDto));
            }
        }
    }
}
