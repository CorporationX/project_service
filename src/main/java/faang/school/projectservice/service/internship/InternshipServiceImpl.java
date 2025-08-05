package faang.school.projectservice.service.internship;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipFilterDto;
import faang.school.projectservice.apimodel.InternshipStatus;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidator internshipValidator;
    private final List<Filter<Internship, InternshipFilterDto>> filters;

    @Override
    public InternshipDto create(Long projectId, InternshipDto dto) {
        internshipValidator.validateCreateDto(dto);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        internshipValidator.validateMentorShip(project, dto);

        Internship internship = internshipMapper.toEntity(dto);
        internship.setProject(project);

        TeamMember mentor = new TeamMember();
        mentor.setId(dto.getMentorId().longValue());
        internship.setMentorId(mentor);

        List<TeamMember> interns = dto.getTraineeIds().stream()
                .map(id -> {
                    TeamMember member = new TeamMember();
                    member.setId(id.longValue());
                    return member;
                })
                .toList();

        internship.setInterns(interns);

        Internship saved = internshipRepository.save(internship);
        return internshipMapper.toDto(saved);
    }

    @Override
    public InternshipDto update(Long id, InternshipDto dto) {
        Internship existing = internshipRepository.getRequiredById(id);

        internshipValidator.validateUpdateDto(existing, dto);

        existing.setStatus(internshipMapper.map(dto.getStatus()));
        existing.setEndDate(dto.getEndDate().toLocalDateTime());

        if (dto.getStatus() == InternshipStatus.COMPLETED || dto.getStatus() == InternshipStatus.FAILED) {
            internshipValidator.applyCompletionLogic(existing);
        }

        Internship saved = internshipRepository.save(existing);
        return internshipMapper.toDto(saved);
    }

    @Override
    public List<InternshipDto> findAll() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public InternshipDto findById(Long id) {
        return internshipRepository.findById(id)
                .map(internshipMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found"));
    }

    @Override
    public List<InternshipDto> findByProject(InternshipFilterDto filterDto) {
        Long projectId = filterDto.getProjectId();

        List<Internship> internships = internshipRepository.findAllByProjectId(projectId);

        Stream<Internship> filterStream = internships.stream();
        for (Filter<Internship, InternshipFilterDto> filter : filters) {
            if (filter.isApplicable(filterDto)) {
                filterStream = filter.filter(filterStream, filterDto);
            }
        }

        return filterStream
                .map(internshipMapper::toDto)
                .toList();
    }

}