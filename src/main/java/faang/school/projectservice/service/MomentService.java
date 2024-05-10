package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.repository.MomentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;

    public void create(MomentDto moment) {
        //ToDo Проверить, что у момента есть название.
        // Моменты можно создавать только для незакрытого проекта.
        // Момент могут шерить несколько проектов.
        momentRepository.save(moment);
    }
}
