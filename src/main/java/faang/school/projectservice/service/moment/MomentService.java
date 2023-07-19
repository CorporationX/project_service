package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MomentService {
    private MomentRepository momentRepository;
    private MomentMapper momentMapper;

    public MomentDto create(MomentDto momentDto) {
        validateMomentDto(momentDto);
        Moment moment = momentRepository.save(momentMapper.toEntity(momentDto));
        return momentMapper.toDto(moment);
    }

    private void validateMomentDto(MomentDto momentDto) {
        if (momentDto.getProjects().stream().anyMatch(projectDto -> projectDto.getStatus().equals(ProjectStatus.CANCELLED)
                || projectDto.getStatus().equals(ProjectStatus.COMPLETED))) {
            throw new DataValidException("Unable to create moment with closed project");
        }
    }
}
