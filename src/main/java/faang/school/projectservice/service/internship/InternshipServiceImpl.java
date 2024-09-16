package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.InternshipFilters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TaskStatus.DONE;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.INTERN;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final ProjectRepository projectRepository;
    private final List<InternshipFilters> internshipFilters;

    @Override
    public InternshipDto create(InternshipDto internshipDto) {
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(internshipDto.getInterns());
        internship.setMentor(teamMemberMapper.toEntity(internshipDto.getMentor()));
        internship.setProject(projectRepository.getProjectById(internshipDto.getProjectId()));
        internship.getInterns().forEach(intern -> intern.setRoles(List.of(INTERN)));
        internshipRepository.save(internship);
        return internshipDto;
    }

    @Override
    public InternshipDto update(InternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new DataValidationException("Пользователь не найден"));

        internship.setStatus(internshipDto.getStatus());
        List<TeamMember> teamMembers = internship.getInterns();

        if (internship.getStatus() == COMPLETED) {
            for (TeamMember teamMember : teamMembers) {
                boolean check = checkTasks(teamMember, sortTask(internshipDto));
                if (check) {
                    teamMember.setRoles(List.of(DEVELOPER));
                } else {
                    internshipDto.getInterns().remove(teamMember);
                }
            }
        }

        if (internship.getStatus() == IN_PROGRESS) {
            for (TeamMember teamMember : teamMembers) {
                boolean check = checkTasks(teamMember, sortTask(internshipDto));
                if (check) {
                    teamMember.setRoles(List.of(DEVELOPER));
                } else {
                    Period period = Period.between(internshipDto.getStartDate().toLocalDate(), LocalDateTime.now().toLocalDate());
                    long result = period.getDays();
                    if (result > 30) {
                        internshipDto.getInterns().remove(teamMember);
                    }
                }
            }
        }

        internship.setEndDate(internshipDto.getEndDate());
        internship.setUpdatedAt(LocalDateTime.now());
        internship.setDescription(internshipDto.getDescription());
        internship.setName(internshipDto.getName());
        internship.setInterns(new ArrayList<>());

        internshipRepository.save(internship);

        return internshipDto;
    }

    @Override
    public List<InternshipDto> getInternshipByFilter(InternshipFilterDto filters) {
        Stream<Internship> internshipStream = new ArrayList<>(internshipRepository.findAll()).stream();
        return internshipMapper.toDtoList(internshipFilters.stream()
                .filter(f -> f.isApplicable(filters))
                .reduce(internshipStream,
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1,s2) -> s1)
                .toList());
    }

    @Override
    public List<InternshipDto> getAllInternships(InternshipDto internshipDto) {
        return internshipMapper.toDtoList(internshipRepository.findAll());
    }

    @Override
    public InternshipDto getInternshipById(InternshipDto internshipDto) {
        return internshipMapper.toDto(internshipRepository.
                findById(internshipDto.getId()).orElseThrow(
                        () -> new DataValidationException("Стажировка не найдена"))
        );
    }

    private List<Task> parseTask(InternshipDto internshipDto) {
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        List<Task> taskList = project.getTasks();
        List<Project> projects = project.getChildren();

        for (Project p : projects) {
            taskList.addAll(p.getTasks());
        }

        return taskList;
    }

    private HashMap<TeamMember, List<Task>> sortTask(InternshipDto internshipDto) {
        List<Task> allTaskList = parseTask(internshipDto);
        List<TeamMember> interns = internshipDto.getInterns();
        HashMap<TeamMember, List<Task>> tasksByUser = new HashMap<>();

        for (TeamMember member : interns) {
            List<Task> memberTask = allTaskList.stream().filter(f -> f.getPerformerUserId().equals(member.getUserId())).toList();
            tasksByUser.put(member, memberTask);
        }

        return tasksByUser;
    }

    private boolean checkTasks(TeamMember member, HashMap<TeamMember, List<Task>> tasksByUser) {
       List<Task> memberTask = tasksByUser.get(member);

       for (Task task : memberTask) {
           if (!task.getStatus().equals(DONE)) {
               return false;
           }
       }
        return true;
    }
}
