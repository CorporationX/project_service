package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectService projectService;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = momentMapper.toMoment(momentDto);
        Project project = projectService.getProjectById(momentDto.getProjectId());

        moment.setCreatedAt(LocalDateTime.now());
        moment.getProjects().add(project);

        Moment newMoment = momentRepository.save(moment);
        return momentMapper.toMomentDto(newMoment);
    }
}
