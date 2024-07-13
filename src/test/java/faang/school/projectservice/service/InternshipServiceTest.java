package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.task.TaskDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.mapper.TaskMapperImpl;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.mapper.TeamMemberMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;
    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private Validator validator;
    @Spy
    private static InternshipMapper internshipMapper;
            //= Mappers.getMapper(InternshipMapper.class);
    @Spy
    private static TaskMapper taskMapper;
                    //= Mappers.getMapper(TaskMapper.class);
    @Spy
    private static ProjectMapper projectMapper ;
                            //= Mappers.getMapper(ProjectMapper.class);
    @Spy
    private static TeamMemberMapper teamMemberMapper ;
                                    //= Mappers.getMapper(TeamMemberMapper.class);

    private static List<Task> tasksList;
    private static List<TaskDto> taskDtos;

    private static Project project;
    private static ProjectDto projectDto;
    private static TeamMember teamMember;
    private static TeamMemberDto teamMemberDto;
    private static Internship internship;
    private static InternshipDto internshipDto;

    private static LocalDateTime dateTime = LocalDateTime.now();

    @BeforeAll
    public static void init() {
        tasksList = List.of(Task.builder()
                        .id(1L)
                        .name("Task 1")
                        .description("description of task 1")
                        .status(TaskStatus.DONE)
                        .build(),
                Task.builder()
                        .id(2L)
                        .name("Task 2")
                        .description("description of task 2")
                        .status(TaskStatus.CANCELLED)
                        .build(),
                Task.builder()
                        .id(2L)
                        .name("Task 3")
                        .description("description of task 3")
                        .status(TaskStatus.IN_PROGRESS)
                        .build());
        taskDtos = taskMapper.toDto(tasksList);


        project = Project.builder().
                id(1L)
                .name("project")
                .description("description of project")
                .tasks(tasksList)
                .build();
        projectDto = projectMapper.toDto(project);

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .build();
        teamMemberDto = teamMemberMapper.toDto(teamMember);

        internship = Internship.builder()
                .id(1L)
                .project(project)
                .mentorId(teamMember)
                .startDate(dateTime)
                .endDate(dateTime.plusDays(90))
                .description("description of internship")
                .name("internship")
                .status(InternshipStatus.COMPLETED)
                .build();

        internshipDto = internshipMapper.toDto(internship);

    }


    @Test
    public void testCreationEventDto() {
        when(internshipRepository.save(any())).thenReturn(internship);
        Assertions.assertEquals(internshipDto, internshipService.create(internshipDto));
    }

    @Test
    public void testAllTaskDone() {
       // when(internshipRepository.save(any())).thenReturn(internship);


       // Assertions.assertFalse(internshipService.haveAllTasksDone(internshipDto));
    }

}
