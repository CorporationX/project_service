package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InternshipMapperTest {
    private InternshipMapper mapper;
    private InternshipDto dto;
    private Internship entity;

    @BeforeEach
    void setUp() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        Long mentorId = 1L;
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        Long internFirstId = 1L;
        Long internSecondId = 2L;
        TeamMember internFirst = new TeamMember();
        TeamMember internSecond = new TeamMember();
        internFirst.setId(internFirstId);
        internSecond.setId(internSecondId);
        List<Long> internIds = List.of(internFirstId, internSecondId);
        List<TeamMember> interns = List.of(internFirst, internSecond);
        LocalDateTime startDate = LocalDateTime.of(2024, 10, 10, 10, 10);
        LocalDateTime endDate = startDate.plusMonths(2);
        InternshipStatus status = InternshipStatus.IN_PROGRESS;
        String description = "description";
        String name = "name";
        LocalDateTime createAt = LocalDateTime.of(2024, 10, 9, 10, 10);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 10, 9, 10, 10);
        Long createdBy = 1L;
        Long updatedBy = 2L;
        Long scheduleId = 1L;
        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);
        Long internshipId = 1L;

        mapper = new InternshipMapperImpl();
        dto = new InternshipDto(internshipId, projectId, mentorId, internIds, startDate,
                endDate, status, description, name, createdBy, updatedBy, scheduleId);
        entity = new Internship(internshipId, project, mentor, interns, startDate,
                endDate, status, description, name, createAt, updatedAt, createdBy, updatedBy, schedule);
    }

    @Test
    public void testToDto() {
        InternshipDto actual = mapper.toDto(entity);

        assertEquals(dto, actual);
    }

    @Test
    public void testToEntity() {
        Internship entityExp = getExpectationDto();

        Internship entityActual = mapper.toEntity(dto);

        assertEquals(entityExp, entityActual);
    }

    private Internship getExpectationDto() {
        Internship entityExp = new Internship();
        entityExp.setId(dto.getId());
        entityExp.setStartDate(dto.getStartDate());
        entityExp.setEndDate(dto.getEndDate());
        entityExp.setStatus(dto.getStatus());
        entityExp.setDescription(dto.getDescription());
        entityExp.setName(dto.getName());
        entityExp.setCreatedBy(dto.getCreatedBy());
        entityExp.setUpdatedBy(dto.getUpdatedBy());

        return entityExp;
    }


}
