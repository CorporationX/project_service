package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
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

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        internshipValidator.validateServiceSaveInternship(internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        List<TeamMember> teamMembers = internshipDto.getInternsId().stream()
                .map(teamMemberRepository::findById).toList();
        internship.setInterns(teamMembers); // set интерны
        TeamMember teamMember = teamMemberRepository.findById(internshipDto.getMentorId());
        internship.setMentor(teamMember); // set ментор
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        internship.setProject(project); // set проект
        internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toDto(internship);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
        Internship oldInternship = internshipRepository.findById(id).orElseThrow(() -> new DataValidationException("There is not internship with this id"));
        internshipValidator.validateServiceUpdateInternship(oldInternship, internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(getListOfInterns(internshipDto.getInternsId())); //50
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internsDoneTasks(internshipDto.getInternsId()); //60
            TeamRole role = TeamRole.DEVELOPER;
            for (TeamMember intern : interns) {
                intern.setRoles(List.of(role));
            }
            internshipRepository.deleteById(id);
        } else {
            return internshipMapper.toDto(internshipRepository.save(internship));
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
        List<TeamMember> secondListOfInterns = new ArrayList<>();
        for (Long aLong : allInternsOnInternship) {
            TeamMember intern = teamMemberRepository.findById(aLong);
            if (checkTaskDone(intern)) {
                secondListOfInterns.add(intern);
            }
        }
        return secondListOfInterns;
    }

    public boolean checkTaskDone(TeamMember teamMember) {
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
}
