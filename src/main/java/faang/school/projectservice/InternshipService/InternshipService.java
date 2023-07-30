package faang.school.projectservice.InternshipService;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final List<InternshipFilter> filterList;

    private void validateListOfInternsAndThereIsMentor(InternshipDto internshipDto) {
        if (internshipDto.getInternsId() == null) {
            throw new DataValidationException("Can't create an internship without interns");
        }
        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plus(3, ChronoUnit.MONTHS))) {
            throw new DataValidationException("Internship cannot last more than 3 months");
        }
        if (internshipDto.getMentorId() == null) {
            throw new DataValidationException("There is not mentor for interns!");
        }
    }

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        validateListOfInternsAndThereIsMentor(internshipDto);
        Internship internship = internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toDto(internship);
    }

    private void validateUpdateInternship(Internship internship, InternshipDto internshipDto) {
        if (internship.getStatus() == null || internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException("Internship is over!");
        }
        if (internship.getInterns().size() < internshipDto.getInternsId().size()) {
            throw new DataValidationException("Cannot add interns!");
        }
    }

    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
        Internship oldInternship = internshipRepository.getById(id);
        validateUpdateInternship(oldInternship, internshipDto);
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

    public InternshipDto findInternshipById(long id) {
        return internshipMapper.toDto(internshipRepository.getById(id));
    }

    public List<InternshipDto> findAllInternships() {
        List<Internship> ents = internshipRepository.findAll();
        return ents.stream().map(internshipMapper::toDto).toList();
    }

    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        List<InternshipDto> list= findAllInternships();
        list.removeIf(dto -> !dto.getProjectId().equals(projectId));
        filter(filterDto, list);
        return list;
    }

    public void filter(InternshipFilterDto filter, List<InternshipDto> dtoList) {
        filterList.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(dtoList, filter));
    }
}
