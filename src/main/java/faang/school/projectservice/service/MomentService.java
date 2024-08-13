package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;

    public Moment createMoment(MomentDto momentDto) {
        return momentRepository.save(momentMapper.toEntity(momentDto));
    }

    public MomentDto addMoment(MomentDto momentDto) {
        Moment moment = momentMapper.toEntity(momentDto);
        return momentMapper.toDto(momentRepository.save(moment));
    }
}
