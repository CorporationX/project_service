package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = momentMapper.toMoment(momentDto);
        moment.setDate(LocalDateTime.now());
        Moment newMoment = momentRepository.save(moment);
        return momentMapper.toMomentDto(newMoment);
    }
}
