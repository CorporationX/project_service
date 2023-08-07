package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.exceptions.InternshipValidationException;
import faang.school.projectservice.filters.InternshipFilters.InternshipFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipRepository internshipRepository;
    private final TaskRepository taskRepository;
    private final InternshipMapper internshipMapper;
    private final List <InternshipFilter> internshipFilters;

    // Стажировка ВСЕГДА относится к какому-то одному проекту.
//                internshipDto.getProjectId() > 1 &&
//                        internshipDto.getProjectId() < 1

    private void internshipBusinessValidation(InternshipDto internshipDto) {
        if (internshipDto.getProjectId() == null) {
            throw new InternshipValidationException("Invalid project id");
        }
        if (internshipDto.getInternsId().size() == 0) {
            throw new InternshipValidationException("Empty list of interns");
        }
        if (Period.between(internshipDto.getStartDate().toLocalDate(),
                internshipDto.getEndDate().toLocalDate()).getMonths() > 3) {
            throw new InternshipValidationException("Invalid internship period");
        }
        if (teamMemberJpaRepository.findByUserIdAndProjectId(internshipDto.getMentorId(),
                internshipDto.getProjectId()) == null) {
            throw new InternshipValidationException("Invalid mentor");
        }
    }

    public InternshipDto internshipCreation(InternshipDto internshipDto) {
        internshipBusinessValidation(internshipDto);
        return internshipMapper.toInternshipDto(internshipRepository.save(internshipMapper.toInternship(internshipDto)));
    }

    public List<InternshipDto> gettingAllInternshipsAccordingToFilters(InternshipFilterDto internshipFilterDto) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();
        List <Internship> listOfInternshipFilters = internshipFilters.stream()
                .filter(internshipFilter -> internshipFilter.isInternshipDtoValid(internshipFilterDto))
                .flatMap(internshipFilter -> internshipFilter.filterInternshipDto(internshipStream, internshipFilterDto)).toList();
        return listOfInternshipFilters.stream().map(internshipMapper::toInternshipDto).toList();
    }

    public List <InternshipDto> gettingAllInternships (){
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toInternshipDto).toList();
    }

    public InternshipDto getInternshipById(Long id){
        //return internshipMapper.toInternshipDto(internshipRepository.getReferenceById(id));
        return internshipMapper.toInternshipDto(internshipRepository.getById(id));
    }


    public InternshipDto internshipUpdate(InternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new InternshipValidationException("Invalid internship Id"));
        Project project = internship.getProject();
        internshipBusinessValidation(internshipDto);
        if (internship.getStatus().equals(COMPLETED)) {
            for (TeamMember teamMember : internship.getInterns()) {
                for (Stage stage : teamMember.getStages()) {
                    for (Task task : stage.getTasks()) {
                        if (task.getStatus().equals(TaskStatus.DONE)) {
                            //team member обладает списком ролей , по логике значит
                            //у него мб несколько ролей, значит у него ужес разу все роли
                            //как жить ?
                            //сложно, но можно❤️)))
                        }
                    }
                }

            }

        }

        if (internship.getStatus().equals(IN_PROGRESS)) {
            List<TeamMember> interns = internship.getInterns();
            interns.forEach(intern -> {
                List<TaskStatus> tasksOfIntern = taskRepository.findAllByProjectIdAndPerformerId(internship.getProject().getId(), intern.getId())
                        .stream()
                        .map(task -> task.getStatus())
                        .toList();
                if (tasksOfIntern.stream().allMatch(task -> task.equals(TaskStatus.DONE))) {
                    //переписать будто стажер получает статус как у ментора
                    intern.addRole(TeamRole.JUNIOR);
                    intern.addRole(TeamRole.INTERN);

                } else {
                    project.getTeam().getTeamMembers().remove(intern);
                }
            });
        }
        return internshipDto;
    }
}




//    Internship internship = internshipRepository.findById(internshipDto.getId())
//            .orElseThrow(() -> new IllegalArgumentException("Invalid internship"));
//    Project project = internship.getProject();
//        if (internship.getStatus().equals(IN_PROGRESS)) {
//                List<TeamMember> interns = internship.getInterns();
//        interns.forEach(intern -> {
//        List<TaskStatus> tasksOfIntern = taskRepository.findAllByProjectIdAndPerformerId(internship.getProject().getId(), intern.getId())
//        .stream()
//        .map(task -> task.getStatus())
//        .toList();
//        if (tasksOfIntern.stream().allMatch(task -> task.equals(TaskStatus.DONE))) {
//        //переписать будто стажер получает статус как у ментора
//        intern.addRole(TeamRole.JUNIOR);
//        intern.addRole(TeamRole.INTERN);
//
//        } else {
//        project.getTeam().getTeamMembers().remove(intern);
//        }
//        });
//        }
//        return internshipDto;
//        }

//        internshipDto.getUpdatedAt()
//        Long project = internshipDto.getProjectId();;
//        project.setId(internshipDto.getProjectId());
//        List<Task> teamTasks = project.getTasks();
//        () -> teamTasks.forEach(task -> {
//            task.getPerformerUserId();
//        })
//                .filter(task -> task.getPerformerUserId() == internshipDto.getId())
//                .toList();
//internshipDto.setStatus();

//        internshipRepository.findById(id).stream().forEach(task -> {
//            task.getPerformerUserId();
//        })
//    }


//private boolean internshipValidation(InternshipDto internshipDto) {
//        return internshipDto.getProjectId() != null &&
//                internshipDto.getProjectId() < 2 &&
//                internshipDto.getInternsId().size() > 0 &&
//                Period.between(internshipDto.getStartDate().toLocalDate(),
//                        internshipDto.getEndDate().toLocalDate()).getMonths() < 3 &&
//        (teamMemberJpaRepository.findByUserIdAndProjectId(internshipDto.getMentorId(), internshipDto.getProjectId()) != null);
//    }

//❤️