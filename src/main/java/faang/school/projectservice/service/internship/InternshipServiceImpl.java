package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Override
    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkInternshipBeforeCreate(internshipDto);
        Project project = projectRepository.getReferenceById(internshipDto.getProjectId());
//        internshipDto.get
//        internshipDto.getInternsId().stream().forEach(intern->teamMembers.add(new TeamMember()));
        return internshipMapper.toDto(internshipRepository.save(internshipMapper.toEntity(internshipDto)));
    }

    @Override
    public InternshipDto updateInternship(InternshipDto internshipDto) {
        Internship internship = internshipRepository.getReferenceById(internshipDto.getId());

        return internshipMapper.toDto(internship);
    }

    @Override
    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        Stream<Internship> internship = internshipRepository.findAll().stream();
        internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(internship, filters));
        return internshipMapper.toDto(internship.toList());
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        List<Internship> internships = internshipRepository.findAll().stream().toList();
        return internshipMapper.toDto(internships);
    }

    @Override
    public InternshipDto getInternship(Long id) {
        Internship internship = internshipRepository.getReferenceById(id);
        return internshipMapper.toDto(internship);
    }

    private void checkInternshipBeforeCreate(InternshipDto internshipDto) {
        Project project = getProjectById(internshipDto.getProjectId());
        ProjectStatus projectStatus = project.getStatus();
        if (Objects.equals(projectStatus, ProjectStatus.ON_HOLD) ||
                Objects.equals(projectStatus, ProjectStatus.CANCELLED)||
                        Objects.equals(projectStatus, ProjectStatus.COMPLETED) ) {
            throw new DataValidationException(String.format("It is not possible to add an internship to a project " +
                    "with the status: %s",projectStatus ));
        }

        List<TeamMember> teamMembers = teamMemberRepository.findByProjectId(internshipDto.getProjectId());
        if (!teamMembers.stream().anyMatch(teamMember -> teamMember.getId() == internshipDto
                .getMentorId())) {
            throw new DataValidationException(String.format("Mentor with id %d not from project %d team",
                    internshipDto.getMentorId(), internshipDto.getProjectId()));
        }
    }

    private Project getProjectById(Long id) {
        return projectRepository.getReferenceById(id);
    }
}
