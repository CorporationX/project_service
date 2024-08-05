package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internshipFilterImpl.InternshipNameFilter;
import faang.school.projectservice.filter.internshipFilterImpl.InternshipStatusFilter;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.jpa.ScheduleRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipDataPreparerTest {
    private InternshipDataPreparer internshipDataPreparer;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Spy
    InternshipFilter internshipNameFilter = new InternshipNameFilter();
    @Spy
    InternshipFilter internshipStatusFilter = new InternshipStatusFilter();

    private InternshipContainer container = new InternshipContainer();

    private InternshipMapper mapper = new InternshipMapperImpl();
    private Internship entity;
    private InternshipDto dto;

    @BeforeEach
    void setUp() {
        internshipDataPreparer = new InternshipDataPreparer(teamMemberRepository, projectRepository, scheduleRepository,
                List.of(internshipNameFilter, internshipStatusFilter));
    }

    @Test
    void testPrepareEntityForCreate() {
        // given
        prepareObjectsForCreate();
        Internship halfEntity = mapper.toEntity(dto);

        //when
        Internship entityActual = internshipDataPreparer.prepareEntityForCreate(dto, halfEntity);

        // then
        entity.setCreatedAt(entityActual.getCreatedAt());
        entity.setUpdatedAt(entityActual.getUpdatedAt());
        assertEquals(entity, entityActual);
    }

    @Test
    void testPrepareEntityForUpdate() {
        // given
        Internship entityExp = prepareObjectsForUpdate();
        when(teamMemberRepository.findById(dto.getMentorId())).thenReturn(entityExp.getMentor());
        when(scheduleRepository.findById(dto.getScheduleId())).thenReturn(Optional.of(entityExp.getSchedule()));

        // when
        Internship entityActual = internshipDataPreparer.prepareEntityForUpdate(dto, entity);

        // then
        entityExp.setUpdatedAt(entityActual.getUpdatedAt());
        assertEquals(entityExp, entityActual);
    }

    @Test
    void testFilterInternships() {
        // given
        List<Internship> internshipList = prepareInternshipsForFilter();

        String filterName = "filter";
        InternshipStatus filterStatus = InternshipStatus.COMPLETED;
        InternshipFilterDto filters = new InternshipFilterDto();
        filters.setName(filterName);
        filters.setStatus(filterStatus);

        Internship internshipExp = internshipList.get(2);
        internshipExp.setName(filterName);
        int sizeExp = 1;

        // when
        List<Internship> internshipsActual = internshipDataPreparer.filterInternships(internshipList, filters);

        // then
        assertEquals(sizeExp, internshipsActual.size());
        assertEquals(internshipExp, internshipsActual.get(0));
    }

    @Test
    void evaluationInterns() {
        // given
        prepareTasksEntity();
        TeamRole role = TeamRole.DEVELOPER;
        int rolesSizeSuccessExp = 1;
        int rolesSizeFailExp = 0;
        TeamMember failedIntern = entity.getInterns().get(0);
        TeamMember successfulIntern = entity.getInterns().get(1);

        // when
        internshipDataPreparer.evaluationInterns(entity, role);

        // then
        assertEquals(rolesSizeFailExp, failedIntern.getRoles().size());
        assertEquals(rolesSizeSuccessExp, successfulIntern.getRoles().size());
        assertEquals(role, successfulIntern.getRoles().get(0));
    }

    private void prepareTasksEntity() {
        Long id = 0L;
        TaskStatus taskStatus = TaskStatus.DONE;
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Task task = new Task();
            task.setId(++id);
            task.setStatus(taskStatus);

            tasks.add(task);
        }
        tasks.get(0).setStatus(TaskStatus.IN_PROGRESS);

        Stage stageFirstIntern = Stage.builder()
                .tasks(tasks.subList(0, 2))
                .build();

        Stage stageSecondIntern = Stage.builder()
                .tasks(tasks.subList(2, 4))
                .build();

        TeamMember internFirst = TeamMember.builder()
                .id(container.firstInternId())
                .stages(List.of(stageFirstIntern))
                .roles(new ArrayList<>(List.of(TeamRole.INTERN)))
                .build();

        TeamMember internSecond = TeamMember.builder()
                .id(container.secondInternId())
                .stages(List.of(stageSecondIntern))
                .roles(new ArrayList<>(List.of(TeamRole.INTERN)))
                .build();

        entity = Internship.builder()
                .interns(new ArrayList<>(List.of(internFirst, internSecond)))
                .build();
    }

    private void prepareObjectsForCreate() {
        TeamMember mentor = TeamMember.builder()
                .id(container.mentorId())
                .build();

        Schedule schedule = Schedule.builder()
                .id(container.scheduleId())
                .build();

        boolean mentorIsMember = true;
        Project project = container.project(mentor, mentorIsMember);

        List<TeamMember> interns = container.getInterns();
        List<Long> internIds = new ArrayList<>(List.of(interns.get(0).getId(), interns.get(1).getId()));

        dto = InternshipDto.builder()
                .mentorId(container.mentorId())
                .projectId(container.projectId())
                .scheduleId(container.scheduleId())
                .internIds(internIds)
                .startDate(container.startDate())
                .endDate(container.endDate())
                .status(container.statusInProgress())
                .description(container.description())
                .name(container.name())
                .createdBy(container.createdBy())
                .updatedBy(container.createdBy())
                .build();

        entity = Internship.builder()
                .mentor(mentor)
                .project(project)
                .schedule(schedule)
                .interns(interns)
                .startDate(container.startDate())
                .endDate(container.endDate())
                .status(container.statusInProgress())
                .description(container.description())
                .name(container.name())
                .createdAt(container.createAt())
                .updatedAt(container.createAt())
                .createdBy(container.createdBy())
                .updatedBy(container.createdBy())
                .build();

        when(teamMemberRepository.findById(internIds.get(0))).thenReturn(interns.get(0));
        when(teamMemberRepository.findById(internIds.get(1))).thenReturn(interns.get(1));
        when(teamMemberRepository.findById(dto.getMentorId())).thenReturn(mentor);
        when(projectRepository.getProjectById(dto.getProjectId())).thenReturn(project);
        when(scheduleRepository.findById(dto.getScheduleId())).thenReturn(Optional.of(schedule));
    }

    private Internship prepareObjectsForUpdate() {
        TeamMember mentor = TeamMember.builder()
                .id(container.mentorId())
                .build();

        TeamMember updatedMentor = TeamMember.builder()
                .id(mentor.getId() + 1)
                .build();

        Schedule schedule = Schedule.builder()
                .id(container.scheduleId())
                .build();

        Schedule updatedSchedule = Schedule.builder()
                .id(schedule.getId() + 1)
                .build();

        boolean mentorIsMember = true;
        Project project = container.project(mentor, mentorIsMember);

        List<TeamMember> interns = container.getInterns();
        List<Long> internIds = new ArrayList<>(List.of(interns.get(0).getId(), interns.get(1).getId()));

        LocalDateTime updatedEndDate = container.endDate().plusDays(1);
        String updatedDescription = container.description() + " update";
        String updatedName = container.name() + " update";

        dto = InternshipDto.builder()
                .id(container.internshipId())
                .mentorId(updatedMentor.getId())
                .projectId(container.projectId())
                .scheduleId(updatedSchedule.getId())
                .internIds(internIds)
                .startDate(container.startDate())
                .endDate(updatedEndDate)
                .status(container.statusCompleted())
                .description(updatedDescription)
                .name(updatedName)
                .createdBy(container.createdBy())
                .updatedBy(container.updatedBy())
                .build();

        entity = Internship.builder()
                .id(container.internshipId())
                .mentor(mentor)
                .project(project)
                .schedule(schedule)
                .interns(interns)
                .startDate(container.startDate())
                .endDate(container.endDate())
                .status(container.statusInProgress())
                .description(container.description())
                .name(container.name())
                .createdAt(container.createAt())
                .updatedAt(container.createAt())
                .createdBy(container.createdBy())
                .updatedBy(container.createdBy())
                .build();

        when(teamMemberRepository.findById(dto.getMentorId())).thenReturn(updatedMentor);
        when(scheduleRepository.findById(dto.getScheduleId())).thenReturn(Optional.of(updatedSchedule));

        return Internship.builder()
                .id(entity.getId())
                .mentor(updatedMentor)
                .project(entity.getProject())
                .schedule(updatedSchedule)
                .interns(entity.getInterns())
                .startDate(entity.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus())
                .description(dto.getDescription())
                .name(dto.getName())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    private List<Internship> prepareInternshipsForFilter() {
        Internship firstInternship = Internship.builder()
                .name(container.name())
                .status(container.statusInProgress())
                .build();

        Internship secondInternship = Internship.builder()
                .name(container.name())
                .status(container.statusInProgress())
                .build();

        Internship thirdInternship = Internship.builder()
                .name(container.name())
                .status(container.statusCompleted())
                .build();

        return List.of(firstInternship, secondInternship, thirdInternship);
    }
}