package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipDtoValidatorTest {
    @InjectMocks
    private InternshipDtoValidator validator;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;

    private InternshipContainer container = new InternshipContainer();

    @Test
    void testValidateMentorWithInvalidMentor() {
        // given
        TeamMember mentor = TeamMember.builder()
                .id(container.mentorId())
                .build();
        boolean mentorIsMember = false;
        Project project = container.project(mentor, mentorIsMember);
        InternshipDto dto = InternshipDto.builder()
                .mentorId(container.mentorId())
                .projectId(container.projectId())
                .build();
        when(teamMemberRepository.findById(dto.getMentorId())).thenReturn(mentor);
        when(projectRepository.getProjectById(dto.getProjectId())).thenReturn(project);

        // then
        assertThrows(DataValidateException.class, () -> validator.validateMentorIsMember(dto));
    }

    @Test
    void testValidateMentorWithValidMentor() {
        // given
        TeamMember mentor = TeamMember.builder()
                .id(container.mentorId())
                .build();
        boolean mentorIsMember = true;
        Project project = container.project(mentor, mentorIsMember);
        InternshipDto dto = InternshipDto.builder()
                .mentorId(container.mentorId())
                .projectId(container.projectId())
                .build();
        when(teamMemberRepository.findById(dto.getMentorId())).thenReturn(mentor);
        when(projectRepository.getProjectById(dto.getProjectId())).thenReturn(project);

        // then
        assertDoesNotThrow(() -> validator.validateMentorIsMember(dto));
    }

    @Test
    void testCheckCompletedStatusFail() {
        // given
        InternshipStatus statusComplete = InternshipStatus.COMPLETED;
        InternshipDto dto = InternshipDto.builder()
                .status(statusComplete)
                .build();
        Internship entity = Internship.builder()
                .status(statusComplete)
                .build();

        // then
        assertThrows(DataValidateException.class, () -> validator.checkCompletedStatus(dto, entity));
    }

    @Test
    void testCheckCompletedStatusSuccess() {
        // given
        InternshipDto dto = InternshipDto.builder()
                .status(container.statusCompleted())
                .build();
        Internship entity = Internship.builder()
                .status(container.statusInProgress())
                .build();

        // when
        boolean actual = validator.checkCompletedStatus(dto, entity);

        // then
        assertTrue(actual);
    }

    @Test
    void testValidateStartEndDate() {
        // given
        InternshipDto dtoWithEndBeforeStart = InternshipDto.builder()
                .startDate(container.startDate())
                .endDate(container.startDate().minusDays(1))
                .build();

        InternshipDto dtoWithEndBeforeNow = InternshipDto.builder()
                .startDate(container.startDate().minusMonths(container.DURATION_INTERNSHIP_IN_MONTH + 1))
                .endDate(LocalDateTime.now().minusHours(1))
                .build();

        InternshipDto dtoWithInvalidDuration = InternshipDto.builder()
                .startDate(container.startDate())
                .endDate(container.endDate().minusMinutes(5))
                .build();

        InternshipDto validDto = InternshipDto.builder()
                .startDate(container.startDate())
                .endDate(container.endDate())
                .build();

        // then
        assertThrows(DataValidateException.class, () -> validator.validateStartEndDate(dtoWithEndBeforeStart));
        assertThrows(DataValidateException.class, () -> validator.validateStartEndDate(dtoWithEndBeforeNow));
        assertThrows(DataValidateException.class, () -> validator.validateStartEndDate(dtoWithInvalidDuration));
        assertDoesNotThrow(() -> validator.validateStartEndDate(validDto));
    }

    @Test
    void testCheckChangeInterns() {
        // given
        List<TeamMember> interns = container.getInterns();
        List<Long> validInternIds = new ArrayList<>(List.of(interns.get(0).getId(), interns.get(1).getId()));
        List<Long> changedInternIds = new ArrayList<>(List.of(interns.get(0).getId()));

        InternshipDto dtoWithChangedInterns = InternshipDto.builder()
                .internIds(changedInternIds)
                .build();

        InternshipDto validDto = InternshipDto.builder()
                .internIds(validInternIds)
                .build();

        Internship entity = Internship.builder()
                .interns(interns)
                .build();

        // then
        assertThrows(DataValidateException.class, () -> validator.checkChangeInterns(dtoWithChangedInterns, entity));
        assertDoesNotThrow(() -> validator.checkChangeInterns(validDto, entity));
    }
}