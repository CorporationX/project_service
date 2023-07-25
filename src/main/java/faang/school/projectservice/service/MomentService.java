package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exceptions.MomentExistingException;
import faang.school.projectservice.filters.FilterMomentDto;
import faang.school.projectservice.filters.MomentFilter;
import faang.school.projectservice.filters.MomentMapper;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilter;

    public Moment createMoment(MomentDto momentDto) {
        return momentRepository.save(momentMapper.dtoToMoment(momentDto));
    }

    public void updateMoment(MomentDto momentDto) {
        Moment deprecatedMoment = momentRepository.findById(momentDto.getId())
                .orElseThrow(() -> new NullPointerException("Such moment wasn't found"));
        Moment updatedMoment = momentMapper.updateMomentFromDto(momentDto, deprecatedMoment);
        momentRepository.save(updatedMoment);
    }

    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto) {
        return momentFilter.stream()
                .filter(filter -> filter.isApplicable(filterMomentDto))
                .flatMap(filter -> filter.apply(momentRepository.findAll().stream(), filterMomentDto))
                .map(momentMapper::momentToDto)
                .toList();
    }

    public List<MomentDto> getAllMoments() {
        return momentMapper.listMomentToDto(momentRepository.findAll());
    }

    public MomentDto getMoment(long momentId) {
        return momentMapper.momentToDto(momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentExistingException(ErrorMessages.NO_SUCH_MOMENT)));
    }
}
