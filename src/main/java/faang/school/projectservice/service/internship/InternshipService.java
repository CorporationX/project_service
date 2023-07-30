package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.internship.InternshipValidator;
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
    private final List<InternshipFilter> filterList;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        InternshipValidator.createInternshipServiceValidation(internshipDto);
        return internshipMapper.toDto(internshipRepository.save(internshipMapper.toEntity(internshipDto)));
    }

    public InternshipDto updateInternship(long id, InternshipDto internshipDto) {
        Internship old = internshipRepository.getById(id);
        InternshipValidator.updateInternshipServiceValidation(old, internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(getInterns(internshipDto.getInterns()));

        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internsResults(internshipDto.getInterns());
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

    public InternshipDto findInternshipById(long id) {
        return internshipMapper.toDto(internshipRepository.getById(id));
    }

    public List<InternshipDto> findAllInternships() {
        List<Internship> ents = internshipRepository.findAll();
        return ents.stream().map(internshipMapper::toDto).toList();
    }

    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        List<InternshipDto> list = findAllInternships();
        list.removeIf(dto -> !dto.getProjectId().equals(projectId));
        filter(filterDto, list);
        return list;
    }

    public List<TeamMember> internsResults(List<Long> interns) {
        int size = interns.size();
        List<TeamMember> res = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            TeamMember intern = teamMemberRepository.findById(interns.get(i));
            if (isAllTAsksDone(intern)) {
                res.add(intern);
            }
        }
        return res;
    }

    public boolean isAllTAsksDone(TeamMember member) {
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

    public List<TeamMember> getInterns(List<Long> interns) {
        int size = interns.size();
        List<TeamMember> res = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            TeamMember intern = teamMemberRepository.findById(interns.get(i));
            res.add(intern);
        }
        return res;
    }

    private void filter(InternshipFilterDto filter, List<InternshipDto> dtoList) {
        filterList.stream()
                .filter((fil) -> fil.isApplicable(filter))
                .forEach((fil) -> fil.apply(dtoList, filter));
    }
}
