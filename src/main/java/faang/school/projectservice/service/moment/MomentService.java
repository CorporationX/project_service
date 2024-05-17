package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validation.moment.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentValidator momentValidator;


    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.momentProjectValidation(momentDto);
        momentValidator.projectNotCancelledValidator(momentDto.getProjectIds());

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    @Transactional
    public MomentDto update(MomentDto momentDto, Long momentId) {
        findMomentFindById(momentId);
//todo  Если добавляется новый партнёр, то должен обновиться список участников и наоборот: если добавляется участник из другого проекта, то проект автоматически добавляется в партнеры по моменту.
        momentDto.setId(momentId);

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

//todo Получить все моменты проекта с фильтрами по дате(? наверное типа по месяцу…) и проектам-партнерам.

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMoments() {
        return momentMapper.toListDto(momentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public MomentDto getMomentById(Long momentId) {
        return momentMapper.toDto(findMomentFindById(momentId));
    }

    private Moment findMomentFindById(Long id) {
        return momentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Moment not found"));
    }
}
