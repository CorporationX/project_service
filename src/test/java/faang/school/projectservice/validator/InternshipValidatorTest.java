package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.ProjectDto;
import faang.school.projectservice.dto.internship.TeamMemberDto;
import faang.school.projectservice.exception.internship.InternshipValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.intership.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
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
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private InternshipRepository internshipRepository;
    private InternshipDto internshipDto;

    private static final long INTERNSHIP_ID_IS_ONE = 1L;
    private static final long MENTOR_ID_ONE = 1L;
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
        public void validateInternshipExistsDoNotThrowExceptionTest() {
            when(internshipRepository.findById(INTERNSHIP_ID_IS_ONE))
                    .thenReturn(Optional.of(Internship.builder().build()));

            assertDoesNotThrow(() -> internshipValidator.validateInternshipExists(INTERNSHIP_ID_IS_ONE));
        }

        @Test
        public void validateInternshipHaveProjectAndInternsDoNotThrowExceptionTest() {
            internshipDto = InternshipDto.builder()
                    .project(ProjectDto.builder().build())
                    .interns(List.of(TeamMemberDto.builder().build()))
                    .build();

            assertDoesNotThrow(() -> internshipValidator.validateInternshipHaveProjectAndInterns(internshipDto));
        }

        @Test
        public void validateInternshipDurationDoNotThrowExceptionTest() {
            internshipDto = InternshipDto.builder()
                    .startDate(VALID_START_DATE)
                    .endDate(VALID_END_DATE)
                    .build();

            assertDoesNotThrow(() -> internshipValidator.validateInternshipDuration(internshipDto));
        }

        @Test
        public void validateInternshipGotMentorDoNotThrowExceptionTest() {
            internshipDto = InternshipDto.builder()
                    .mentorId(TeamMemberDto.builder().id(MENTOR_ID_ONE).build())
                    .build();
            when(teamMemberRepository.findById(internshipDto.getMentorId().getId()))
                    .thenReturn(TeamMember.builder().build());

            assertDoesNotThrow(() -> internshipValidator.validateInternshipGotMentor(internshipDto));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        public void validateInternshipExistsThrowExceptionTest() {
            when(internshipRepository.findById(INTERNSHIP_ID_IS_ONE)).thenReturn(Optional.empty());

            assertThrows(InternshipValidationException.class, () ->
                    internshipValidator.validateInternshipExists(INTERNSHIP_ID_IS_ONE));
        }

        @Test
        public void validateInternshipHaveNullProjectOrEmptyInternsThrowsExceptionTest() {
            InternshipDto internshipDtoNullProject = InternshipDto.builder()
                    .interns(List.of(TeamMemberDto.builder().build()))
                    .build();
            InternshipDto internshipDtoNullList = InternshipDto.builder()
                    .project(ProjectDto.builder().build())
                    .build();
            assertThrows(InternshipValidationException.class, () ->
                    internshipValidator.validateInternshipHaveProjectAndInterns(internshipDtoNullProject));
            assertThrows(InternshipValidationException.class, () ->
                    internshipValidator.validateInternshipHaveProjectAndInterns(internshipDtoNullList));
        }

        @Test
        public void validateInternshipDurationThrowExceptionTest() {
            internshipDto = InternshipDto.builder()
                    .startDate(INVALID_START_DATE)
                    .endDate(INVALID_END_DATE)
                    .build();

            assertThrows(InternshipValidationException.class, () ->
                    internshipValidator.validateInternshipDuration(internshipDto));
        }

        @Test
        public void validateInternshipGotMentorThrowExceptionTest() {
            internshipDto = InternshipDto.builder()
                    .mentorId(TeamMemberDto.builder().build())
                    .build();
            when(teamMemberRepository.findById(internshipDto.getMentorId().getId()))
                    .thenThrow(EntityNotFoundException.class);

            assertThrows(EntityNotFoundException.class, () ->
                    internshipValidator.validateInternshipGotMentor(internshipDto));
        }
    }
}
