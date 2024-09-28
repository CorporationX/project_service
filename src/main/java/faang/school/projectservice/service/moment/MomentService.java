package faang.school.projectservice.service.moment;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.model.Moment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;

    @Autowired
    public MomentService(MomentRepository momentRepository, MomentMapper momentMapper) {
        this.momentRepository = momentRepository;
        this.momentMapper = momentMapper;
    }

    public MomentDto createMoment(MomentDto momentDto) {
        if (momentDto.getName() == null || momentDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Moment name cannot be empty");
        }
        Moment moment = momentMapper.toEntity(momentDto);
        Moment savedMoment = momentRepository.save(moment);
        return momentMapper.toDto(savedMoment);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        if (momentDto.getId() == null) {
            throw new IllegalArgumentException("Moment ID cannot be null");
        }
        Moment moment = momentMapper.toEntity(momentDto);
        Moment updatedMoment = momentRepository.save(moment);
        return momentMapper.toDto(updatedMoment);
    }

    public List<MomentDto> getAllMoments() {
        return momentRepository.findAll().stream()
                .map(momentMapper::toDto)
                .collect(Collectors.toList());
    }

    public MomentDto getMomentById(long id) {
        Optional<Moment> moment = momentRepository.findById(id);
        return moment.map(momentMapper::toDto).orElse(null);
    }

    public List<MomentDto> getMomentsByFilters(Date date, List<Long> partnerProjectIds) {
        return momentRepository.findAll().stream()
                .filter(moment -> (date == null || moment.getDate().equals(date)) &&
                        (partnerProjectIds == null || moment.getPartnerProjectIds().containsAll(partnerProjectIds)))
                .map(momentMapper::toDto)
                .collect(Collectors.toList());
    }
}