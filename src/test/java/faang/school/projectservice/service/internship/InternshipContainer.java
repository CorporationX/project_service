package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InternshipContainer {
    public static final int DURATION_INTERNSHIP_IN_MONTH = 3;

    private Long internshipId;
    private Long projectId;
    private Long mentorId;
    private Long firstInternId;
    private Long secondInternId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String name;
    private InternshipStatus statusInProgress;
    private InternshipStatus statusCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long scheduleId;
    private Long teamId;

    public InternshipContainer() {
        Long tempId = 0L;
        internshipId = ++tempId;
        projectId = ++tempId;
        mentorId = ++tempId;
        firstInternId = ++tempId;
        secondInternId = ++tempId;
        startDate = LocalDateTime.of(2024, 7, 18, 10, 10, 10);
        startDate = LocalDateTime.now();
        endDate = startDate.plusMonths(DURATION_INTERNSHIP_IN_MONTH);
        statusInProgress = InternshipStatus.IN_PROGRESS;
        statusCompleted = InternshipStatus.COMPLETED;
        description = "description";
        name = "name";
        createdAt = startDate;
        updatedAt = startDate.plusDays(10);
        createdBy = ++tempId;
        updatedBy = ++tempId;
        scheduleId = ++tempId;
        teamId = ++tempId;
    }

    public Long internshipId() {
        return internshipId;
    }

    public Long mentorId() {
        return mentorId;
    }

    public LocalDateTime createAt() {
        return createdAt;
    }

    public Long projectId() {
        return projectId;
    }

    public Long scheduleId() {
        return scheduleId;
    }

    public Project project(TeamMember mentor, boolean isMember) {
        TeamMember member = new TeamMember();
        member.setId(mentor.getId() + 1);
        List<TeamMember> members = new ArrayList<>(List.of(member));

        if (isMember) {
            members.add(mentor);
        }

        Team team = new Team();
        team.setId(teamId);
        team.setTeamMembers(members);

        Project project = new Project();
        project.setId(projectId);
        project.setTeams(List.of(team));

        return project;
    }

    public Long createdBy() {
        return createdBy;
    }

    public LocalDateTime startDate() {
        return startDate;
    }

    public LocalDateTime endDate() {
        return endDate;
    }

    public String description() {
        return description;
    }

    public String name() {
        return name;
    }

    public Long updatedBy() {
        return updatedBy;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public InternshipStatus statusInProgress() {
        return statusInProgress;
    }

    public InternshipStatus statusCompleted() {
        return statusCompleted;
    }

    public Long firstInternId() {
        return firstInternId;
    }

    public Long secondInternId() {
        return secondInternId;
    }

    public ArrayList<TeamMember> getInterns() {
        TeamMember internFirst = TeamMember.builder()
                .id(firstInternId())
                .build();

        TeamMember internSecond = TeamMember.builder()
                .id(secondInternId())
                .build();
        return new ArrayList<>(List.of(internFirst, internSecond));
    }

    public TeamMember mentor() {
        return TeamMember.builder()
                .id(mentorId)
                .build();
    }

    public List<Long> internIds() {
        return new ArrayList<>(List.of(firstInternId, secondInternId));
    }

    public InternshipDto validDto() {
        return InternshipDto.builder()
                .id(internshipId)
                .mentorId(mentorId)
                .projectId(projectId)
                .scheduleId(scheduleId)
                .internIds(internIds())
                .startDate(startDate)
                .endDate(endDate)
                .status(statusInProgress)
                .description(description)
                .name(name)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .build();
    }

    public InternshipFilterDto filters() {
        return InternshipFilterDto.builder()
                .name("name")
                .status(InternshipStatus.IN_PROGRESS)
                .build();
    }
}
