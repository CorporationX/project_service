package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exceptions.MomentExistingException;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.filters.moments.MomentFilter;
import faang.school.projectservice.filters.mappers.MomentMapper;
import faang.school.projectservice.controller.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilter;

    @Transactional
    public Moment createMoment(MomentDto momentDto) {
        return momentRepository.save(momentMapper.dtoToMoment(momentDto));
    }

    @Transactional
    public void updateMoment(MomentDto momentDto) {
        Moment deprecatedMoment = momentRepository.findById(momentDto.getId())
                .orElseThrow(() -> new NullPointerException(
                        String.format("moment with %d wasn't found", momentDto.getId())));
        Moment updatedMoment = momentMapper.updateMomentFromDto(momentDto, deprecatedMoment);
        momentRepository.save(updatedMoment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto) {
        Stream<Moment> allMoments = momentRepository.findAll().stream();
        return momentFilter.stream()
                .filter(filter -> filter.isApplicable(filterMomentDto))
                .flatMap(filter -> filter.apply(allMoments, filterMomentDto))
                .map(momentMapper::momentToDto)
                .toList();
    }

    public List<MomentDto> getAllMoments() {
        return momentMapper.listMomentToDto(momentRepository.findAll());
    }

    public MomentDto getMoment(long momentId) {
        return momentMapper.momentToDto(momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentExistingException(
                        String.format("moment with %d wasn't found", momentId))));
    }
}
