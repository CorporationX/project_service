package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.dto.client.InternshipUpdateDto;
import faang.school.projectservice.exceptions.InternshipValidationException;
import faang.school.projectservice.filters.InternshipFilters.InternshipFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    public InternshipDto internshipCreation(InternshipDto internshipDto) {
        internshipBusinessValidation(internshipDto);
        return internshipMapper.toInternshipDto(internshipRepository.save(internshipMapper.toInternship(internshipDto)));
    }

    public InternshipDto updateInternship(InternshipUpdateDto internshipUpdateDto, Long idInternship) {
        Internship internship = ifInternshipRepositoryContainsInternshipId(idInternship);

        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            for (TeamMember teamMember : internship.getInterns()) {
                updateInterns(teamMember, internship);
            }
        }
        internshipMapper.update(internshipUpdateDto, internship);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public InternshipDto updateInternBeforeInternshipEnd(Long idInternship, Long internsId) {
        Internship internship = ifInternshipRepositoryContainsInternshipId(idInternship);
        TeamMember teamMember = teamMemberRepository.findById(internsId);

        isInternshipContainsTeamMember(internship, teamMember);
        updateInterns(teamMember, internship);

        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public InternshipDto deleteIntern(Long idInternship, Long internsId) {
        Internship internship = ifInternshipRepositoryContainsInternshipId(idInternship);
        TeamMember teamMember = teamMemberRepository.findById(internsId);

        isInternshipContainsTeamMember(internship, teamMember);
        internship.getInterns().remove(teamMember);

        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public List<InternshipDto> gettingAllInternshipsAccordingToFilters(InternshipFilterDto internshipFilterDto) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();
        List<InternshipFilter> listOfInternshipFilters = internshipFilters.stream()
                .filter(internshipFilter -> internshipFilter.isInternshipDtoValid(internshipFilterDto))
                .toList();

        for (InternshipFilter filter : listOfInternshipFilters) {
            internshipStream = filter.filterInternshipDto(internshipStream, internshipFilterDto);
        }
        return internshipStream.map(internshipMapper::toInternshipDto).toList();
    }

    public List<InternshipDto> gettingAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toInternshipDto).toList();
    }

    public InternshipDto getInternshipById(Long id) {
        //return internshipMapper.toInternshipDto(internshipRepository.getReferenceById(id));
        return internshipMapper.toInternshipDto(internshipRepository.getById(id));
    }

    private void internshipBusinessValidation(InternshipDto internshipDto) {
        if (internshipDto.getProjectId() == null) {
            throw new InternshipValidationException("Invalid project id");
        }
        if (internshipDto.getInternsId().isEmpty()) {
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

    private Internship ifInternshipRepositoryContainsInternshipId(Long idInternship) {
        return internshipRepository.findById(idInternship)
                .orElseThrow(() -> new InternshipValidationException("There is not internship with this id"));
    }

    private void isInternshipContainsTeamMember(Internship internship, TeamMember teamMember) {
        if (!internship.getInterns().contains(teamMember)) {
            throw new InternshipValidationException("There is not intern with this id");
        }
    }

    private void updateInterns(TeamMember teamMember, Internship internship) {
        Project project = internship.getProject();
        List<Task> teamMemberTask = project.getTasks().stream()
                .filter(task -> task.getPerformerUserId().equals(teamMember.getId()))
                .toList();
        boolean flag = false;
        for (Task task : teamMemberTask) {
            if (task.getStatus() != TaskStatus.DONE) {
                internship.getInterns().remove(teamMember);
                flag = true;
                break;
            }
        }
        if (!flag) {
            teamMember.getRoles().remove(TeamRole.INTERN);
            teamMember.getRoles().add(TeamRole.JUNIOR);
        }
    }
}

//----------------------------------------------------------------------------------------------------------------------
// Стажировка ВСЕГДА относится к какому-то одному проекту.
//                internshipDto.getProjectId() > 1 &&
//                        internshipDto.getProjectId() < 1

//----------------------------------------------------------------------------------------------------------------------
//Trials to create Update method for Internship:

//Обновить стажировку.
// Если стажировка завершена, то стажирующиеся должны получить
// новые роли на проекте, если прошли, и быть удалены из списка участников проекта,
// если не прошли. Участник считается прошедшим стажировку, если все запланированные
// задачи выполнены. После старта стажировки нельзя добавлять новых стажёров.
// Стажировку можно пройти досрочно или досрочно быть уволенным.

//1 какая мб причина досрочно быть уволенным?
//2 teamMemberRepository.deleteById(teamMember.getId()); удаление в принципе из репозитория?
//то есть удаление в принципе из бд ... и как такой челв принципе потом сможет добавляться на другие стажировки?
//3 После старта стажировки нельзя добавлять новых стажёров - как бы мы их добавляли?
//4 Нужно ли пeредавать InternshipDto
//5 Можно ли использовать update у Mapper'a

//    public Set<InternshipDto> gettingAllInternshipsAccordingToFilters(InternshipFilterDto internshipFilterDto) {
//        Stream<Internship> internshipStream = internshipRepository.findAll().stream();
//        Set <Internship> listOfInternshipFilters = internshipFilters.stream()
//                .filter(internshipFilter -> internshipFilter.isInternshipDtoValid(internshipFilterDto))
//                .flatMap(internshipFilter -> internshipFilter.filterInternshipDto(internshipStream, internshipFilterDto))
//                .collect(Collectors.toSet());
//        return listOfInternshipFilters.stream().map(internshipMapper::toInternshipDto).collect(Collectors.toSet());
//    }

//    public InternshipDto updateInternship(InternshipDto internshipDto, Long idInternship) {
//        Internship internship = internshipRepository.findById(idInternship)
//                .orElseThrow(() -> new InternshipValidationException("There is not internship with this id"));
//
//        Project project = internship.getProject();
//
//        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
//            for (TeamMember teamMember : internship.getInterns()) {
//                List<Task> teamMemberTask = project.getTasks().stream()
//                        .filter(task -> task.getPerformerUserId().equals(teamMember.getId()))
//                        .toList();
//
//                boolean flag = false;
//
//                for (Task task : teamMemberTask) {
//                    if (task.getStatus() != TaskStatus.DONE) {
//                        teamMemberRepository.deleteById(teamMember.getId());
//                        flag = true;
//                        break;
//                    }
//                }
//
//                if (!flag) {
//                    teamMember.getRoles().remove(TeamRole.INTERN);
//                    for (TeamRole role : internship.getMentorId().getRoles()) {
//                        teamMember.getRoles().add(role);
//                    }
//                }
//            }
//        }
//        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
//    }
//}


//    public List<TeamMember> getListOfInterns(List<Long> allInternsOnInternship) {
//        List<TeamMember> secondListOfInterns = new ArrayList<>();
//        for (Long aLong : allInternsOnInternship) {
//            TeamMember intern = teamMemberRepository.findById(aLong);
//            secondListOfInterns.add(intern);
//        }
//        return secondListOfInterns;
//    }
//
//    public List<TeamMember> internsDoneTasks(List<Long> allInternsOnInternship) {
//        List<TeamMember> secondListOfInterns = new ArrayList<>();
//        for (Long aLong : allInternsOnInternship) {
//            TeamMember intern = teamMemberRepository.findById(aLong);
//            if (checkTaskDone(intern)) {
//                secondListOfInterns.add(intern);
//            }
//        }
//        return secondListOfInterns;
//    }
//
//    public boolean checkTaskDone(TeamMember teamMember) {
//        Project project = teamMember.getTeam().getProject();
//        List<Task> tasks = project.getTasks();
//        for (Task task : tasks) {
//            if (task.getPerformerUserId().equals(teamMember.getUserId())) {
//                if (task.getStatus().equals(TaskStatus.DONE)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public void validateServiceUpdateInternship(Internship oldInternship, InternshipDto internshipDto) {
//        validateServiceSaveInternship(internshipDto);
//        if (oldInternship.getStatus() == null || oldInternship.getStatus().equals(InternshipStatus.COMPLETED)) {
//            throw new InternshipValidationException("Internship is over!"); //?
//        }
//        if (oldInternship.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
//            throw new InternshipValidationException("Can't add new interns!");
//        }
//    }
//
//    public void validateServiceSaveInternship(InternshipDto internshipDto) {
//        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plus(3, ChronoUnit.MONTHS))) {
//            throw new InternshipValidationException("Internship cannot last more than 3 months!"); //?
//        }
//        if (internshipDto.getMentorId() == null || internshipDto.getMentorId() < 1){
//            throw new InternshipValidationException("There is not mentor for internship!");
//        }
//        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()){
//            throw new InternshipValidationException("There is not interns for internship!");
//        }
//    }
//}


//    public InternshipDto internshipUpdate(InternshipDto internshipDto) {
//        Internship internship = internshipRepository.findById(internshipDto.getId())
//                .orElseThrow(() -> new InternshipValidationException("Invalid internship Id"));
//        Project project = internship.getProject();
//        internshipBusinessValidation(internshipDto);
//        if (internship.getStatus().equals(COMPLETED)) {
//            for (TeamMember teamMember : internship.getInterns()) {
//                for (Stage stage : teamMember.getStages()) {
//                    for (Task task : stage.getTasks()) {
//                        if (task.getStatus().equals(TaskStatus.DONE)) {
//                            //team member обладает списком ролей , по логике значит
//                            //у него мб несколько ролей, значит у него ужес разу все роли
//                            //как жить ?
//                        }
//                    }
//                }
//            }
//        }
//
//        if (internship.getStatus().equals(IN_PROGRESS)) {
//            List<TeamMember> interns = internship.getInterns();
//            interns.forEach(intern -> {
//                List<TaskStatus> tasksOfIntern = taskRepository.findAllByProjectAndPerformerUserId(internship.getProject(), intern.getId())
//                        .stream()
//                        .map(task -> task.getStatus())
//                        .toList();
//                if (tasksOfIntern.stream().allMatch(task -> task.equals(TaskStatus.DONE))) {
//                    //переписать будто стажер получает статус как у ментора
////                    intern.addRole(TeamRole.JUNIOR);
////                    intern.addRole(TeamRole.INTERN);
//
//                } else {
//                    //project.getTeam().getTeamMembers().remove(intern);
//                }
//            });
//        }
//        return internshipDto;
//    }}

//
//    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
//        Internship oldInternship = internshipRepository.findById(id)
//                .orElseThrow(() -> new InternshipValidationException("There is not internship with this id"));
//        validateServiceUpdateInternship(oldInternship, internshipDto);
//        Internship internship = internshipMapper.toInternship(internshipDto);
//        internship.setInterns(getListOfInterns(internshipDto.getInternsId()));
//
//        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
//            List<TeamMember> interns = internsDoneTasks(internshipDto.getInternsId());
//            TeamRole role = TeamRole.DEVELOPER;
//
//            for (TeamMember intern : interns) {
//                intern.setRoles(List.of(role));
//            }
//
//            internshipRepository.deleteById(id);
//        } else {
//            return internshipMapper.toInternshipDto(internshipRepository.save(internship));
//        }
//        return internshipDto;
//    }

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