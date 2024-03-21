package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.filter.moment.DataRangeFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.moment.ValidatorMoment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final ValidatorMoment validatorMoment;
    private final MomentMapper momentMapper;

    @Transactional
    public void createMoment(MomentDto momentDto) {
        validatorMoment.ValidatorMomentName(momentDto);
        validatorMoment.ValidatorOpenProject(momentDto);
        validatorMoment.ValidatorMomentProject(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        momentRepository.save(moment);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        Moment moment = momentRepository.findById(momentDto.getId()).orElseThrow(() -> new EntityNotFoundException("moment id not found"));
        updateUsers(momentDto,moment);
        updateProjects(momentDto, moment);
        momentRepository.save(moment);
        return momentDto;
    }
    private void updateProjects(MomentDto momentDto,Moment moment){
        List<Long> oldUserIds = moment.getUserIds();
        List<Long> newUserIds = momentDto.getUserIds();
        List<Long> newProjectIds = momentDto.getProjectIds();
        List<Project> newProjects = projectRepository.findAllByIds(newProjectIds);
        if (!oldUserIds.equals(newUserIds)) {
            moment.setProjects(newProjects);
        }
    }
    private void updateUsers(MomentDto momentDto,Moment moment){
        List<Long> oldProjectIds = moment.getProjects().stream()
                .map(Project::getId)
                .toList();
        List<Long> newProjectIds = momentDto.getProjectIds();
        if (!oldProjectIds.equals(newProjectIds)) {
            moment.setUserIds(momentDto.getUserIds().stream()
                    .distinct()
                    .toList());
        }
    }
    public MomentDto getAllMomentsByDate(MomentDto momentDto){
        //6.1 ,2e видео
        DataRangeFilter.
    }
}


