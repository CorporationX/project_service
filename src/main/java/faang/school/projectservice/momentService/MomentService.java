package faang.school.projectservice.momentService;

import faang.school.projectservice.filterMoment.FilterMoment;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.momentDto.MomentDto;
import faang.school.projectservice.momentMapper.MomentMapper;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final FilterMoment filterMoment;
    private final MomentMapper momentMapper;
    public MomentDto createMoment(MomentDto momentDto){
        filterMoment.filterMomentName();
        filterMoment.filterOpenProject();
        return momentMapper.toMomentDto(momentRepository.save(momentDt));
    }
    @Transactional
    public void updateMoment(Long id, Long projectId, Long userIds){
        Moment moment = momentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found"));
        Project projectAdd = projectRepository.getProjectById(projectId);
        if (!moment.getProjects().contains(projectAdd)) {
            moment.getUserIds().add(userIds);
        }
       if (!moment.getUserIds().contains(userIds)) {
            moment.getProjects().add(projectAdd);
        }
        momentRepository.save(moment);
    }

    public void filterMoment(Moment data, Project project){

    }
}
