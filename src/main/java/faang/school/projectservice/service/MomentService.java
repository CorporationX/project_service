package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = momentRepository.save(momentMapper.toEntity(momentDto));
        return momentMapper.toDto(moment);
    }
}
