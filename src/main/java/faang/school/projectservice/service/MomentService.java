package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentValidator momentValidator;
    private final MomentMapper momentMapper;

    public MomentDto createMoment(MomentDto momentDto) {
        momentValidator.checkIsProjectClosed(momentDto.getProjectIds());
        momentDto.setDate(LocalDateTime.now());
        Moment moment = momentMapper.toEntity(momentDto);
        momentRepository.save(moment);

        return momentMapper.toDto(moment);
    }
}
