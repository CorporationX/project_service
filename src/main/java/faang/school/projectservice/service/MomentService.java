package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exceptions.MomentExistingException;
import faang.school.projectservice.filters.FilterMomentDto;
import faang.school.projectservice.filters.FiltersDto;
import faang.school.projectservice.filters.MomentMapper;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<FiltersDto> filtersDto;

    public void createMoment(MomentDto momentDto) {
        momentRepository.save(momentMapper.dtoToMoment(momentDto));
    }

    public void updateMoment(MomentDto momentDto) {
        momentRepository.save(momentMapper.dtoToMoment(momentDto));
    }

    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto) {
        return filtersDto.stream()
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
