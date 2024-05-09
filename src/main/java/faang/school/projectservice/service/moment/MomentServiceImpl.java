package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {
    private final MomentMapper mapper;
    private final MomentRepository momentRepository;

    @Override
    public MomentDto create(MomentDto moment) {
        Moment entity = mapper.toEntity(moment);
        Moment saved = momentRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public MomentDto createFromInitiative(Initiative initiative) {
        MomentDto momentDto = mapper.toDtoFromInitiative(initiative);

        return create(momentDto);
    }
}
