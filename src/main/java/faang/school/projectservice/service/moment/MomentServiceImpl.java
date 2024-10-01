package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.MomentService;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;

    public MomentDto createMoment(MomentDto momentDto) {
        List<Project> projects = projectRepository.findAllByIds(momentDto.getProjectIds());
        if (projects.stream()
                .anyMatch(this::isProjectClosed)) {
            throw new DataValidationException("Moment can only be created for open projects"    );
        }

        Moment moment = momentMapper.toEntity(momentDto);
        moment.setProjects(projects);

        Moment savedMoment = momentRepository.save(moment);
        return momentMapper.toDto(savedMoment);
    }

    public MomentDto updateMoment(Long id, MomentDto momentDto) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Moment not found"));

        moment.setName(momentDto.getName());
        moment.setDescription(momentDto.getDescription());
        moment.setDate(momentDto.getDate());

        List<Project> projects = projectRepository.findAllByIds(momentDto.getProjectIds());
        moment.setProjects(projects);

        Moment updatedMoment = momentRepository.save(moment);
        return momentMapper.toDto(updatedMoment);

    }

    public MomentDto getMomentById(Long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Moment not found"));

        return momentMapper.toDto(moment);
    }

    public List<MomentDto> getAllMoment() {
        List<Moment> moments = momentRepository.findAll();
        return moments.stream()
                .map(momentMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean isProjectClosed(Project project) {
        return project.getStatus() == ProjectStatus.COMPLETED ||
                project.getStatus() == ProjectStatus.CANCELLED;
    }
}
