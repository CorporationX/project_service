package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
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
    private final InternshipValidator validator;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        validator.internshipServiceValidation(internshipDto);
        return internshipMapper.toDto(internshipRepository.save(internshipMapper.toEntity(internshipDto)));
    }

    public InternshipDto updateInternship(long id, InternshipDto internshipDto) {
        Internship internship = internshipMapper.toEntity(findInternshipById(id));
        validator.updateInternshipServiceValidation(internship, internshipDto);
        internship = internshipMapper.toEntity(internshipDto);

        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internshipDto.getInterns().stream().map(teamMemberRepository::findById).toList();
            List<TeamMember> acceptedInterns = internsResults(interns);
            for (TeamMember intern : interns) {
                if (acceptedInterns.contains(intern)) {
                    intern.setRoles(List.of(TeamRole.DEVELOPER));
                } else {
                    intern.setRoles(List.of());
                }
            }
        } else {
            return internshipMapper.toDto(internshipRepository.save(internship));
        }
        return internshipDto;
    }

    public InternshipDto findInternshipById(long id) {
        return internshipMapper.toDto(internshipRepository.findById(id).orElseThrow(() ->
                new DataValidationException("Internship not found! Incorrect internship id")));
    }

    public List<InternshipDto> findAllInternships() {
        List<Internship> internships = internshipRepository.findAll();
        return internships.stream().map(internshipMapper::toDto).toList();
    }

    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        List<InternshipDto> list = findAllInternships();
        list.removeIf(dto -> !dto.getProjectId().equals(projectId));
        filter(filterDto, list);
        return list;
    }

    private List<TeamMember> internsResults(List<TeamMember> interns) {
        List<TeamMember> res = new ArrayList<>(interns.size());
        interns.forEach(intern -> {;
            if (isAllTAsksDone(intern)) {
                res.add(intern);
            }
        });

        return res;
    }

    private boolean isAllTAsksDone(TeamMember member) {
        Project project = member.getTeam().getProject();
        List<Task> tasks = project.getTasks();
        for (Task task : tasks) {
            if (task.getPerformerUserId().equals(member.getUserId())) {
                if (!task.getStatus().equals(TaskStatus.DONE)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void filter(InternshipFilterDto filter, List<InternshipDto> dtoList) {
        filterList.stream()
                .filter((fil) -> fil.isApplicable(filter))
                .forEach((fil) -> fil.apply(dtoList, filter));
    }
}
