package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;

    public MomentDto addMoment(MomentDto momentDto) {
        Moment moment = momentMapper.MomentDtoToMoment(momentDto);
        momentRepository.save(moment);
        return momentMapper.MomentToMomentDto(moment);
    }
}
