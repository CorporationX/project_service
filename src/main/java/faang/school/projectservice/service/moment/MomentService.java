package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment momentEntity = momentMapper.toEntity(momentDto);
        fillMoment(momentEntity, momentDto.getUserIds());

        return momentMapper.toDto(momentRepository.save(momentEntity));
    }

    private void fillMoment(Moment moment, List<Long> projectsIds) {
        moment.setProjects(projectRepository.findAllByIds(projectsIds));
    }
}
