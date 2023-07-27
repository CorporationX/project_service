package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exceptions.MomentExistingException;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.filters.moments.MomentFilter;
import faang.school.projectservice.filters.mappers.MomentMapper;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        List<Moment> allMoments = momentRepository.findAll();
        List<List<Moment>> afterFilters = momentFilter.stream()
                .filter(filter -> filter.isApplicable(filterMomentDto))
                .map(filter -> filter.apply(allMoments.stream(), filterMomentDto).toList())
                .toList();
        List<Moment> result = afterFilters.get(0);
        if(result.size() == 0){
            throw new RuntimeException(ErrorMessages.NO_SUCH_MOMENTS);
        }
        if(afterFilters.size() == 1){
            return momentMapper.listMomentToDto(result);
        }
        for (int i = 1; i < afterFilters.size(); i++) {
            result = result.stream()
                    .filter(afterFilters.get(i)::contains)
                    .toList();
        }
        return momentMapper.listMomentToDto(result);
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
