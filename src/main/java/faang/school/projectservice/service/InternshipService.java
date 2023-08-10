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
import java.util.Iterator;
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

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        internshipValidator.validateServiceSaveInternship(internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        List<TeamMember> teamMembers = internshipDto.getInternsId().stream()
                .map(teamMemberRepository::findById).toList();
        internship.setInterns(teamMembers);
        TeamMember teamMember = teamMemberRepository.findById(internshipDto.getMentorId());
        internship.setMentor(teamMember);
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        internship.setProject(project);
        internshipRepository.save(internship);
        return internshipMapper.toDto(internship);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There is not internship with this id"));
        internshipValidator.validateServiceUpdateInternship(internship, internshipDto);
        Internship internshipForUpdate = internshipMapper.toEntity(internshipDto);
        internshipForUpdate.setInterns(getListOfInterns(internshipDto.getInternsId()));
        if (internshipForUpdate.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internsDoneTasks(internshipDto.getInternsId());
            interns.forEach(intern -> intern.setRoles(List.of(TeamRole.DEVELOPER)));
            internshipRepository.deleteById(id);
        } else {
            return internshipMapper.toDto(internshipRepository.save(internshipForUpdate));
        }
        return internshipDto;
    }

    public List<TeamMember> getListOfInterns(List<Long> allInternsOnInternship) {
        List<TeamMember> secondListOfInterns = new ArrayList<>();
        for (Long aLong : allInternsOnInternship) {
            TeamMember intern = teamMemberRepository.findById(aLong);
            secondListOfInterns.add(intern);
        }
        return secondListOfInterns;
    }

    public List<TeamMember> internsDoneTasks(List<Long> allInternsOnInternship) {
        List<TeamMember> listWithThePassedParticipants = new ArrayList<>();
        Iterator<Long> iterator = allInternsOnInternship.iterator();

        while (iterator.hasNext()) {
            Long aLong = iterator.next();
            TeamMember intern = teamMemberRepository.findById(aLong);
            if (checkTasksDone(intern)) {
                listWithThePassedParticipants.add(intern);
            } else {
                iterator.remove();
            }
        }
        return listWithThePassedParticipants;
    }

    public Project getProject(TeamMember teamMember) {
        return teamMember.getTeam().getProject();
    }
    public boolean checkTasksDone(TeamMember teamMember) {
        Project project = getProject(teamMember);
        List<Task> tasks = project.getTasks();
        return tasks.stream()
                .anyMatch(task -> task.getPerformerUserId().equals(teamMember.getUserId()) && task.getStatus().equals(TaskStatus.DONE));
    }

    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        List<InternshipDto> listOfInternship = getAllInternships();
        listOfInternship.removeIf(dto -> !dto.getProjectId().equals(projectId));
        filter(filterDto, listOfInternship);
        return listOfInternship;
    }

    public void filter(InternshipFilterDto filter, List<InternshipDto> dtoList) {
        filterList.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(dtoList, filter));
    }

    public List<InternshipDto> getAllInternships() {
        List<Internship> listOfInternship = internshipRepository.findAll();
        return listOfInternship.stream().map(internshipMapper::toDto).toList();
    }

    public InternshipDto findInternshipById(long id) {
        return internshipMapper.toDto(internshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There is not internship with this id")));
    }
}