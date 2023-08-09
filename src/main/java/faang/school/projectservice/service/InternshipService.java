package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> filterList;

    public InternshipDto saveNewInternship(InternshipDto internshipDto) { //1 создать стажировку
        internshipValidator.validateServiceSaveInternship(internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        List<TeamMember> teamMembers = internshipDto.getInternsId().stream()
                .map(teamMemberRepository::findById).toList();
        internship.setInterns(teamMembers); // set интерны
        TeamMember teamMember = teamMemberRepository.findById(internshipDto.getMentorId());
        internship.setMentor(teamMember); // set ментор
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        internship.setProject(project); // set проект
        internshipRepository.save(internship);
        return internshipMapper.toDto(internship);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto, long id) { //2 обновить стажировку+
        Internship oldInternship = internshipRepository.findById(id).orElseThrow(() -> new DataValidationException("There is not internship with this id"));
        internshipValidator.validateServiceUpdateInternship(oldInternship, internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(getListOfInterns(internshipDto.getInternsId()));
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internsDoneTasks(internshipDto.getInternsId());
            for (TeamMember intern : interns) {
                intern.setRoles(List.of(TeamRole.DEVELOPER));
            }
            internshipRepository.deleteById(id);
        } else {
            return internshipMapper.toDto(internshipRepository.save(internship));
        }
        return internshipDto;
    }

    public List<TeamMember> getListOfInterns(List<Long> allInternsOnInternship) { //2.1
        List<TeamMember> secondListOfInterns = new ArrayList<>();
        for (Long aLong : allInternsOnInternship) {
            TeamMember intern = teamMemberRepository.findById(aLong);
            secondListOfInterns.add(intern);
        }
        return secondListOfInterns;
    }

    public List<TeamMember> internsDoneTasks(List<Long> allInternsOnInternship) { //2.2
        List<TeamMember> listWithThePassedParticipants = new ArrayList<>();
        for (Long aLong : allInternsOnInternship) {
            TeamMember intern = teamMemberRepository.findById(aLong);
            if (checkTaskDone(intern)) { //72
                listWithThePassedParticipants.add(intern);
            }
        }
        return listWithThePassedParticipants;
    }

    public boolean checkTaskDone(TeamMember teamMember) { //2.3+
        Project project = teamMember.getTeam().getProject();
        List<Task> tasks = project.getTasks();
        for (Task task : tasks) {
            if (task.getPerformerUserId().equals(teamMember.getUserId())) {
                if (task.getStatus().equals(TaskStatus.DONE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<InternshipDto> findInternshipsByStatusWithFilter(long projectId, InternshipFilterDto filterDto) { //3 получить все стажировки по статусу+
        List<InternshipDto> listOfInternship = getAllInternships();
        listOfInternship.removeIf(dto -> !dto.getProjectId().equals(projectId));
        filter(filterDto, listOfInternship);
        return listOfInternship;
    }

    public void filter(InternshipFilterDto filter, List<InternshipDto> dtoList) { //3.1
        filterList.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(dtoList, filter));
    }

    public List<InternshipDto> getAllInternships() { //4 получить все стажировки+
        List<Internship> listOfInternship = internshipRepository.findAll();
        return listOfInternship.stream().map(internshipMapper::toDto).toList();
    }

    public InternshipDto findAllInternshipById(long id) { //5 получить стажировку по id+
        return internshipMapper.toDto(internshipRepository.findById(id).orElseThrow(() -> new DataValidationException("There is not internship with this id")));
    }
}