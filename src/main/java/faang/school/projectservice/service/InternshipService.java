package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipMapper internshipMapper;

    private void validateUpdateInternship(Internship internship, InternshipDto internshipDto) {
        if (internship.getStatus() == null || internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException("Internship is over!");
        }
        if (internship.getInterns().size() < internshipDto.getInterns().size()) {
            throw new DataValidationException("Cannot add interns!");
        }
    }

    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
        Internship oldInternship = internshipRepository.getById(id);
        validateUpdateInternship(oldInternship, internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(getListOfInterns(internshipDto.getInterns())); //50
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internsDoneTasks(internshipDto.getInterns()); //60
            TeamRole role = TeamRole.DEVELOPER;
            for (TeamMember intern : interns) {
                intern.setRoles(List.of(role));
            }
            internshipRepository.deleteById(id);
        } else {
            return internshipMapper.toDto(internshipRepository.save(internship));
        }
        return null;
    }

    public List<TeamMember> getListOfInterns(List<Long> interns) {
        int sizeOfListInterns = interns.size();
        List<TeamMember> secondListOfInterns = new ArrayList<>(sizeOfListInterns);
        for (int i = 0; i < sizeOfListInterns; i++) {
            TeamMember intern = teamMemberRepository.findById(interns.get(i));
            secondListOfInterns.add(intern);
        }
        return secondListOfInterns;
    }

    public List<TeamMember> internsDoneTasks(List<Long> interns) {
        int sizeOfListInterns = interns.size();
        List<TeamMember> secondListOfInterns = new ArrayList<>(sizeOfListInterns);
        for (int i = 0; i < sizeOfListInterns; i++) {
            TeamMember intern = teamMemberRepository.findById(interns.get(i));
            if (checkTaskDone(intern)) { //72
                secondListOfInterns.add(intern);
            }
        }
        return secondListOfInterns;
    }

    public boolean checkTaskDone(TeamMember member) {
        List<Stage> stages = member.getStages();
        for (Stage stage : stages) {
            List<Task> tasks = stage.getTasks();
            for (Task task : tasks) {
                if (!task.getStatus().equals(TaskStatus.DONE)) {
                    return false;
                }
            }
        }
        return true;
    }
}
