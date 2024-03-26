package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.filter.InternshipFilter;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestSetUp {
    //test
    TeamMember firstIntern;
    TeamMember secondIntern;
    TeamMember thirdIntern;
    TeamMember forthIntern;
    TeamMember fifthIntern;
    TeamMember firstInternPromoted;
    TeamMember secondInternPromoted;
    TeamMember thirdInternDemoted;
    Task firstTask;
    Task secondTask;
    Task thirdTask;

    Project firstProject;
    Project secondProject;
    Team team1;

    Internship firstInternship;
    Internship secondInternship;
    Internship thirdInternship;
    Internship fourthInternship;

    InternshipDto firstInternshipDto;
    InternshipDto secondInternshipDto;
    InternshipDto thirdInternshipDto;

    List<Internship> internshipsList = new ArrayList<>();
    List<InternshipDto> internshipDtosList = new ArrayList<>();
    Map<Long, List<Task>> map = new HashMap<>();
    Map<Boolean, List<Long>> map1 = new HashMap<>();
    List<Long> succeededUserIds = new ArrayList<>();
    List<Long> failedInterns = new ArrayList<>();
    List<TeamMember> afterPromotion = new ArrayList<>();
    List<TeamMember> afterDemotion = new ArrayList<>();

    InternshipFilterDto firstFilters;
    InternshipFilterDto secondFilters;
    InternshipFilterDto thirdFilters;

    @BeforeEach
    public void init() {
        InternshipFilter filterMock = Mockito.mock(InternshipFilter.class);
        List<InternshipFilter> filters = List.of(filterMock);

        firstIntern = TeamMember.builder()
                .id(11L)
                .userId(12L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER, TeamRole.INTERN)))
                .build();
        secondIntern = TeamMember.builder()
                .id(13L)
                .userId(14L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER, TeamRole.INTERN)))
                .build();
        thirdIntern = TeamMember.builder()
                .id(15L)
                .userId(16L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER, TeamRole.INTERN)))
                .build();

        team1 = Team.builder()
                .id(1123L)
                .teamMembers(new ArrayList<>(List.of(firstIntern, secondIntern, thirdIntern)))
                .project(firstProject)
                .build();


        firstInternPromoted = TeamMember.builder()
                .id(11L)
                .userId(12L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER, TeamRole.ANALYST)))
                .build();
        secondInternPromoted = TeamMember.builder()
                .id(13L)
                .userId(14L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER, TeamRole.ANALYST)))
                .build();
        thirdInternDemoted = TeamMember.builder()
                .id(15L)
                .userId(16L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER)))
                .build();


        firstTask = Task.builder()
                .id(848L)
                .project(firstProject)
                .performerUserId(firstIntern.getUserId())
                .status(TaskStatus.DONE)
                .build();
        secondTask = Task.builder()
                .id(747L)
                .project(firstProject)
                .performerUserId(secondIntern.getUserId())
                .status(TaskStatus.DONE)
                .build();
        thirdTask = Task.builder()
                .id(646L)
                .project(firstProject)
                .performerUserId(thirdIntern.getUserId())
                .status(TaskStatus.IN_PROGRESS)
                .build();

        firstProject = Project.builder()
                .id(144L)
                .tasks(new ArrayList<>(List.of(firstTask, secondTask, thirdTask)))
                .teams(new ArrayList<>(List.of(team1)))
                .build();


        firstInternship = Internship.builder()
                .id(1L)
                .name("First Internship")
                .mentorId(new TeamMember())
                .project(firstProject)
                .startDate(LocalDateTime.of(2024, 1, 1, 1, 1))
                .endDate(LocalDateTime.of(2024, 3, 1, 1, 1))
                .interns(new ArrayList<>(List.of(firstIntern, secondIntern, thirdIntern)))
                .status(InternshipStatus.COMPLETED)
                .role(TeamRole.ANALYST)
                .build();


        secondInternship = Internship.builder()
                .id(1L)
                .name("Second Internship")
                .mentorId(new TeamMember())
                .project(new Project())
                .startDate(LocalDateTime.of(2024, 1, 1, 1, 1))
                .endDate(LocalDateTime.of(2024, 3, 1, 1, 1))
                .interns(new ArrayList<>(List.of(secondIntern, thirdIntern, new TeamMember(), new TeamMember())))
                .status(InternshipStatus.COMPLETED)
                .role(TeamRole.DESIGNER)
                .build();

        thirdInternship = Internship.builder()
                .id(654L)
                .name("Third Internship")
                .mentorId(new TeamMember())
                .project(new Project())
                .startDate(LocalDateTime.of(2024, 1, 1, 1, 1))
                .endDate(LocalDateTime.of(2024, 3, 1, 1, 1))
                .interns(new ArrayList<>(List.of(firstIntern, secondIntern, new TeamMember(), new TeamMember())))
                .status(InternshipStatus.COMPLETED)
                .role(TeamRole.DEVELOPER)
                .build();

        internshipsList.add(firstInternship);
        internshipsList.add(secondInternship);
        internshipsList.add(thirdInternship);

        firstInternshipDto = InternshipDto.builder()
                .id(1L)
                .name("")
                .mentorId(11L)
                .projectId(firstProject.getId())
                .startDate(LocalDateTime.of(2024, 1, 1, 1, 1))
                .endDate(LocalDateTime.of(2024, 3, 1, 1, 1))
                .internsIds(new ArrayList<>(List.of(firstIntern.getUserId(), secondIntern.getUserId(), thirdIntern.getUserId())))
                .status(InternshipStatus.COMPLETED)
                .role(TeamRole.ANALYST)
                .build();

        secondInternshipDto = InternshipDto.builder()
                .id(1L)
                .name("")
                .mentorId(12L)
                .projectId(19L)
                .startDate(LocalDateTime.of(2024, 1, 1, 1, 1))
                .endDate(LocalDateTime.of(2024, 3, 1, 1, 1))
                .internsIds(new ArrayList<>(List.of(secondIntern.getId(), thirdIntern.getId(), 45L, 54L)))
                .status(InternshipStatus.COMPLETED)
                .role(TeamRole.ANALYST)
                .build();

        thirdInternshipDto = InternshipDto.builder()
                .id(654L)
                .name("")
                .mentorId(12L)
                .projectId(1222L)
                .startDate(LocalDateTime.of(2024, 1, 1, 1, 1))
                .endDate(LocalDateTime.of(2024, 3, 1, 1, 1))
                .internsIds(new ArrayList<>(List.of(firstIntern.getId(), secondIntern.getId(), 87L, 89L)))
                .status(InternshipStatus.COMPLETED)
                .role(TeamRole.ANALYST)
                .build();

        internshipDtosList.add(firstInternshipDto);
        internshipDtosList.add(secondInternshipDto);
        internshipDtosList.add(thirdInternshipDto);

        map1.put(false, new ArrayList<>(List.of(thirdIntern.getUserId())));
        map1.put(true, new ArrayList<>(List.of(firstIntern.getUserId(), secondIntern.getUserId())));


        map.put(firstIntern.getUserId(), List.of(firstTask));
        map.put(secondIntern.getUserId(), List.of(secondTask));
        map.put(thirdIntern.getUserId(), List.of(thirdTask));

        succeededUserIds.add(firstIntern.getUserId());
        succeededUserIds.add(secondIntern.getUserId());

        failedInterns.add(thirdIntern.getUserId());

        afterPromotion.add(firstInternPromoted);
        afterPromotion.add(secondInternPromoted);

        afterDemotion.add(thirdInternDemoted);

        firstFilters = InternshipFilterDto.builder()
                .role(TeamRole.ANALYST)
                .internshipStatus(InternshipStatus.IN_PROGRESS)
                .build();
        secondFilters = InternshipFilterDto.builder()
                .role(TeamRole.DEVELOPER)
                .internshipStatus(InternshipStatus.COMPLETED)
                .build();
        thirdFilters = InternshipFilterDto.builder()
                .role(TeamRole.DESIGNER)
                .internshipStatus(InternshipStatus.IN_PROGRESS)
                .build();


        thirdIntern.setTeam(team1);
        thirdInternDemoted.setTeam(null);
        afterDemotion = new ArrayList<>(List.of(thirdInternDemoted));

    }
}