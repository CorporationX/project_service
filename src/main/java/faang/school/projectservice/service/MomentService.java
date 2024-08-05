package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
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

    public Moment createMoment(MomentDto momentDto) {
        return momentRepository.save(momentMapper.momentDtoToMoment(momentDto));
    }

    public MomentDto addMoment(MomentDto momentDto) {
        Moment moment = momentMapper.momentDtoToMoment(momentDto);
        momentRepository.save(moment);
        return momentMapper.momentToMomentDto(moment);
    }
}
