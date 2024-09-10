package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentValidator;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentValidator momentValidator;
    private final ProjectValidator projectValidator;

    public MomentDto createMoment(long momentId) {
       Moment moment = momentRepository.findById(momentId).orElseThrow();
       momentValidator.validateMoment(moment);
       moment.getProjects().forEach(projectValidator::validateProject);
       return momentMapper.toDto(moment);
    }
    public void updateMoment(long momentId) {

    }

}
