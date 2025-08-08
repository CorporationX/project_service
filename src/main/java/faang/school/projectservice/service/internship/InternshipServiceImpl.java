package faang.school.projectservice.service.internship;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipFilterDto;
import faang.school.projectservice.apimodel.InternshipStatus;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.FilterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidator internshipValidator;
    private final List<Filter<Internship, InternshipFilterDto>> filters;
    private final FilterService<Internship, InternshipFilterDto> internshipFilterService;

    @Override
    @Transactional
    public InternshipDto create(Long projectId, InternshipDto dto) {
        internshipValidator.validateCreateDto(dto);

        Project project = projectRepository.getByIdOrThrow(projectId);
        internshipValidator.validateMentorship(project, dto);

        Internship internship = internshipMapper.toEntity(dto);
        internship.setProject(project);

        initializeMembers(internship, dto);

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
        Internship internship = internshipRepository.getRequiredById(id);
        return internshipMapper.toDto(internship);
    }

    @Override
    public List<InternshipDto> findByProject(InternshipFilterDto filterDto) {
        Long projectId = filterDto.getProjectId();
        List<Internship> internships = internshipRepository.findAllByProjectId(projectId);

        List<Internship> filtered = internshipFilterService.getFilteredList(internships, filterDto);

        return filtered.stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    private void initializeMembers(Internship internship, InternshipDto dto) {
        TeamMember mentor = new TeamMember();
        mentor.setId(dto.getMentorId().longValue());
        internship.setMentorId(mentor);

        internship.setInterns(
                dto.getTraineeIds().stream()
                        .map(id -> {
                            TeamMember member = new TeamMember();
                            member.setId(id.longValue());
                            return member;
                        })
                        .toList()
        );
    }
}