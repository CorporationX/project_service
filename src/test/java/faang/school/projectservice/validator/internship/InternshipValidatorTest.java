package faang.school.projectservice.validator.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.internship.AlreadyCompletedException;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class InternshipValidatorTest {

    private final LocalDateTime NOW = LocalDateTime.now();
    private final LocalDateTime TWO_MONTH = LocalDateTime.now().plusMonths(2);
    private final LocalDateTime THREE_MONTH = LocalDateTime.now().plusMonths(3);
    private final LocalDateTime FOUR_MONTH = LocalDateTime.now().plusMonths(4);

    private final Project PROJECT = new Project();
    private final TeamMember MENTOR = new TeamMember();
    private final List<Team> TEAMS = new ArrayList<>();
    private final Team TEAM = new Team();
    private final TeamMember INTERN = new TeamMember();
    private final List<TeamMember> INTERNS = new ArrayList<>();
    private final InternshipStatus COMPLETED = InternshipStatus.COMPLETED;
    private final InternshipStatus IN_PROGRESS = InternshipStatus.IN_PROGRESS;

    private final long MENTOR_ID = 2L;
    private final long INTERN_ID = 3L;

    @Test
    public void validateLengthDate_shouldThrowDataValidationException_whenStartDateIsNull() {
        LocalDateTime start = null;
        LocalDateTime end = TWO_MONTH;
        assertThrows(DataValidationException.class,
                () -> InternshipValidator.validateLengthDate(start, end));
    }

    @Test
    public void validateLengthDate_shouldThrowDataValidationException_whenStartDateIsAfterEndDate() {
        LocalDateTime start = THREE_MONTH;
        LocalDateTime end = TWO_MONTH;
        assertThrows(DataValidationException.class,
                () -> InternshipValidator.validateLengthDate(start, end));
    }

    @Test
    public void validateLengthDate_shouldThrowDataValidationException_whenDurationExceedsThreeMonths() {
        LocalDateTime start = NOW;
        LocalDateTime end = FOUR_MONTH;
        assertThrows(DataValidationException.class,
                () -> InternshipValidator.validateLengthDate(start, end));
    }

    @Test
    public void validateLengthDate_shouldNotThrow_whenDurationDoesNotExceedThreeMonths() {
        LocalDateTime start = NOW;
        LocalDateTime end = THREE_MONTH;
        assertDoesNotThrow(
                () -> InternshipValidator.validateLengthDate(start, end));
    }

    @Test
    public void validateLengthDate_shouldNotThrow_whenDurationIsJustBelowThreeMonths() {
        LocalDateTime start = NOW;
        LocalDateTime end = THREE_MONTH.minusSeconds(1);
        assertDoesNotThrow(
                () -> InternshipValidator.validateLengthDate(start, end));
    }

    @Test
    public void validateLengthDate_shouldNotThrow_whenDurationIsOneSecond() {
        LocalDateTime start = NOW;
        LocalDateTime end = NOW.plusSeconds(1);
        assertDoesNotThrow(
                () -> InternshipValidator.validateLengthDate(start, end));
    }

    @Test
    public void validateMentorBelongsToProject_shouldNotThrow_whenMentorIsInProjectTeam() {
        PROJECT.setTeams(TEAMS);
        MENTOR.setId(MENTOR_ID);
        TEAM.setTeamMembers(List.of(MENTOR));
        TEAMS.add(TEAM);
        assertDoesNotThrow(
                () -> InternshipValidator.validateMentorBelongsToProject(PROJECT, MENTOR));
    }

    @Test
    public void validateMentorBelongsToProject_shouldThrowEntityNotFoundException_whenProjectHasNoTeams() {
        PROJECT.setTeams(null);
        MENTOR.setId(MENTOR_ID);
        assertThrows(EntityNotFoundException.class,
                () -> InternshipValidator.validateMentorBelongsToProject(PROJECT, MENTOR));
    }

    @Test
    public void validateMentorBelongsToProject_shouldThrowEntityNotFoundException_whenProjectTeamsListIsEmpty() {
        PROJECT.setTeams(new ArrayList<>());
        MENTOR.setId(MENTOR_ID);
        assertThrows(EntityNotFoundException.class,
                () -> InternshipValidator.validateMentorBelongsToProject(PROJECT, MENTOR));
    }

    @Test
    public void validateMentorBelongsToProject_shouldThrowEntityNotFoundException_whenMentorIsNotAMemberOfAnyProjectTeam() {
        PROJECT.setTeams(TEAMS);
        MENTOR.setId(MENTOR_ID);
        TEAM.setTeamMembers(List.of());
        TEAMS.add(TEAM);
        assertThrows(EntityNotFoundException.class,
                () -> InternshipValidator.validateMentorBelongsToProject(PROJECT, MENTOR));
    }

    @Test
    public void validateInternsNotEmpty_shouldThrowEntityNotFoundException_whenInternsListIsNull() {
        List<TeamMember> interns = null;
        assertThrows(EntityNotFoundException.class,
                () -> InternshipValidator.validateInternsNotNullAndNotEmpty(interns));
    }

    @Test
    public void validateInternsNotEmpty_shouldThrowEntityNotFoundException_whenInternsListIsEmpty() {
        INTERNS.clear();
        assertThrows(EntityNotFoundException.class,
                () -> InternshipValidator.validateInternsNotNullAndNotEmpty(INTERNS));
    }

    @Test
    public void validateInternsNotNullAndNotEmpty_shouldNotThrow_whenListContainsAtLeastOneIntern() {
        INTERN.setId(INTERN_ID);
        INTERNS.add(INTERN);
        assertDoesNotThrow(
                () -> InternshipValidator.validateInternsNotNullAndNotEmpty(INTERNS));
    }

    @Test
    public void validateIfInternshipIsStatusComplete_shouldNotThrow_whenStatusIsNotComplete() {
        assertDoesNotThrow(
                () -> InternshipValidator.validateIfInternshipIsStatusComplete(IN_PROGRESS));
    }

    @Test
    public void validateIfInternshipIsStatusComplete_shouldThrowAlreadyCompletedException_whenStatusIsComplete() {
        assertThrows(AlreadyCompletedException.class,
                () -> InternshipValidator.validateIfInternshipIsStatusComplete(COMPLETED));
    }
}