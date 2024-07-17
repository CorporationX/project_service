package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internShip.InternshipDtoValidateException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.validator.internShip.InternshipDtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternshipDtoValidatorTest {
    private final InternshipDtoValidator validator = new InternshipDtoValidator();
    private InternshipDto dto;
    private Internship entity;

    @BeforeEach
    void setUp() {
        Long internshipId = 1L;
        Long mentorId = 1L;
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        Project project = createProject(mentor, true);
        Long internFirstId = 1L;
        TeamMember internFirst = new TeamMember();
        internFirst.setId(internFirstId);
        Long internSecondId = 2L;
        TeamMember internSecond = new TeamMember();
        internSecond.setId(internSecondId);
        List<Long> internIds = new ArrayList<>(List.of(internFirstId, internSecondId));
        List<TeamMember> interns = new ArrayList<>(List.of(internFirst, internSecond));
        LocalDateTime startDate = LocalDateTime.of(2024, 5, 5, 5, 5, 5);
        LocalDateTime endDate = LocalDateTime.of(2024, 9, 5, 5, 5, 5);
        InternshipStatus status = InternshipStatus.IN_PROGRESS;
        String description = "description";
        String name = "name";
        LocalDateTime createdAt = LocalDateTime.of(2024, 5, 5, 5, 5, 5);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 5, 5, 5, 5, 5);
        Long createdBy = mentorId;
        Long updatedBy = mentorId;
        Long scheduleId = 1L;
        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);

        entity = new Internship(internshipId, project, mentor, interns, startDate, endDate, status, description, name, createdAt,
                updatedAt, createdBy, updatedBy, schedule);
        dto = new InternshipDto(internshipId, project.getId(), mentorId, internIds, startDate,
                endDate, status, description, name, createdBy, updatedBy, scheduleId);
    }

    @Test
    public void testValidateInternshipDto() {
        InternshipDto dtoWithOutProjectId = getDtoForCreate(OptionCreate.WO_PROJECT_ID);
        InternshipDto dtoWithOutInternIds = getDtoForCreate(OptionCreate.WO_INTERN_IDS);
        InternshipDto dtoWithOutMentorId = getDtoForCreate(OptionCreate.WO_MENTOR_ID);
        InternshipDto dtoInvalidDuration = getDtoForCreate(OptionCreate.INVALID_DURATION);
        InternshipDto dtoInvalidStartDateNull = getDtoForCreate(OptionCreate.START_DATE_NULL);
        InternshipDto dtoInvalidEndDateNull = getDtoForCreate(OptionCreate.END_DATE_NULL);
        InternshipDto dtoInvalidEndBeforeStart = getDtoForCreate(OptionCreate.END_DATE_BEFORE_START);
        InternshipDto dtoInvalidEndBeforeNow = getDtoForCreate(OptionCreate.END_DATE_BEFORE_NOW);

        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoInvalidDuration));
        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoWithOutProjectId));
        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoWithOutInternIds));
        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoWithOutMentorId));
        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoInvalidStartDateNull));
        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoInvalidEndDateNull));
        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoInvalidEndBeforeStart));
        assertThrows(InternshipDtoValidateException.class, () -> validator.validateInternshipDto(dtoInvalidEndBeforeNow));
        assertTrue(validator.validateInternshipDto(dto));
    }

    // Создание dto, на все сценарии валидации при create Internship
    private InternshipDto getDtoForCreate(OptionCreate option) {
        InternshipDto invalidDto = new InternshipDto();
        invalidDto.setProjectId(1L);
        invalidDto.setMentorId(1L);
        invalidDto.setStartDate(LocalDateTime.of(2024, 6, 10, 10, 10));
        invalidDto.setEndDate(LocalDateTime.of(2024, 10, 10, 10, 10));
        invalidDto.setInternIds(List.of(1L, 2L));

        switch (option) {
            case WO_PROJECT_ID -> invalidDto.setProjectId(null);
            case WO_MENTOR_ID -> invalidDto.setMentorId(null);
            case WO_INTERN_IDS -> invalidDto.setInternIds(null);
            case INVALID_DURATION -> invalidDto.setEndDate(invalidDto.getStartDate().plusMonths(2));
            case START_DATE_NULL -> invalidDto.setStartDate(null);
            case END_DATE_NULL -> invalidDto.setEndDate(null);
            case END_DATE_BEFORE_START -> invalidDto.setEndDate(invalidDto.getStartDate().minusDays(1));
            case END_DATE_BEFORE_NOW -> invalidDto.setStartDate(LocalDateTime.now().minusMinutes(2));
        }
        return invalidDto;
    }

    @Test
    public void testValidateMentorIsNotMemberTeam() {
        TeamMember mentor = entity.getMentorId();
        boolean isMember = false;
        Project project = createProject(mentor, isMember);

        assertThrows(InternshipDtoValidateException.class, () -> validator.validateMentorIsMember(mentor, project));
    }

    @Test
    public void testValidateMentorIsMemberTeam() {
        TeamMember mentor = new TeamMember();
        mentor.setId(1L);
        boolean isMember = true;
        Project project = createProject(mentor, isMember);

        assertTrue(validator.validateMentorIsMember(mentor, project));
    }

    // Создание project с ментором и без
    private Project createProject(TeamMember mentor, boolean isMember) {
        TeamMember member = new TeamMember();
        member.setId(mentor.getId() + 1);
        List<TeamMember> members = new ArrayList<>(List.of(member));
        if (isMember) {
            members.add(mentor);
        }

        Team team = new Team();
        team.setId(1L);
        team.setTeamMembers(members);

        Project project = new Project();
        project.setId(1L);
        project.setTeams(List.of(team));

        return project;
    }

    @Test
    public void testCheckChanges() {
        InternshipDto dtoChangeInterns = getDtoForUpdate(OptionUpdate.CHANGE_INTERNS);
        InternshipDto dtoChangeProject = getDtoForUpdate(OptionUpdate.CHANGE_PROJECT);
        InternshipDto dtoChangeEndDate = getDtoForUpdate(OptionUpdate.CHANGE_END_DATE);
        InternshipDto dtoChangeStartDate = getDtoForUpdate(OptionUpdate.CHANGE_START_DATE);
        InternshipDto dtoValidChanges = getDtoForUpdate(OptionUpdate.VALID_CHANGES);

        assertThrows(InternshipDtoValidateException.class, () -> validator.checkChanges(entity, dtoChangeInterns));
        assertThrows(InternshipDtoValidateException.class, () -> validator.checkChanges(entity, dtoChangeProject));
        assertThrows(InternshipDtoValidateException.class, () -> validator.checkChanges(entity, dtoChangeEndDate));
        assertThrows(InternshipDtoValidateException.class, () -> validator.checkChanges(entity, dtoChangeStartDate));
        assertTrue(validator.checkChanges(entity, dtoValidChanges));
        assertTrue(validator.checkChanges(entity, dto));
    }

    // Создание dto для теста сценариев обновления данных
    private InternshipDto getDtoForUpdate(OptionUpdate option) {
        InternshipDto changedDto = new InternshipDto(dto.getId(), dto.getProjectId(), dto.getMentorId(),
                new ArrayList<>(dto.getInternIds()), dto.getStartDate(), dto.getEndDate(), dto.getStatus(),
                dto.getDescription(), dto.getName(), dto.getCreatedBy(), dto.getUpdatedBy(), dto.getScheduleId());

        switch (option) {
            case CHANGE_INTERNS -> {
                Long newInternsId = (long) (dto.getInternIds().size() + 1);
                changedDto.getInternIds().add(newInternsId);
            }
            case CHANGE_PROJECT -> {
                Long newProjectId = dto.getProjectId() + 1;
                changedDto.setProjectId(newProjectId);
            }
            case CHANGE_START_DATE -> {
                LocalDateTime newStartDate = dto.getStartDate().plusMinutes(1);
                changedDto.setStartDate(newStartDate);
            }
            case CHANGE_END_DATE -> {
                LocalDateTime newEndTime = dto.getStartDate().minusDays(1);
                changedDto.setEndDate(newEndTime);
            }
            case VALID_CHANGES -> {
                LocalDateTime newEndTime = dto.getEndDate().plusDays(10);
                String newDescription = "newDescription";
                String newName = "newName";
                Long newScheduleId = dto.getScheduleId() + 1;
                InternshipStatus newStatus = InternshipStatus.COMPLETED;
                changedDto.setEndDate(newEndTime);
                changedDto.setDescription(newDescription);
                changedDto.setName(newName);
                changedDto.setScheduleId(newScheduleId);
                changedDto.setStatus(newStatus);
            }
        }
        return changedDto;
    }

    @Test
    void testCheckStatusThrowsException() {
        InternshipStatus completeStatus = InternshipStatus.COMPLETED;
        dto.setStatus(completeStatus);
        entity.setStatus(completeStatus);

        assertThrows(InternshipDtoValidateException.class, () -> validator.checkCompletedStatus(dto, entity));
    }
}

