package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
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
    private final InternshipMapper internshipMapper;

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        InternshipValidator.validateServiceSaveInternship(internshipDto);
        Internship internship = internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toDto(internship);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
        Internship oldInternship = internshipRepository.getById(id);
        InternshipValidator.validateServiceUpdateInternship(oldInternship, internshipDto);
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
        return null;
    }

    public List<TeamMember> getListOfInterns(List<Long> interns) {
        int sizeOfListInterns = interns.size();
        List<TeamMember> secondListOfInterns = new ArrayList<>(sizeOfListInterns);
        for (Long aLong : interns) {
            TeamMember intern = teamMemberRepository.findById(aLong);
            secondListOfInterns.add(intern);
        }
        return secondListOfInterns;
    }

    public List<TeamMember> internsDoneTasks(List<Long> interns) {
        int sizeOfListInterns = interns.size();
        List<TeamMember> secondListOfInterns = new ArrayList<>(sizeOfListInterns);
        for (Long aLong : interns) {
            TeamMember intern = teamMemberRepository.findById(aLong);
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
