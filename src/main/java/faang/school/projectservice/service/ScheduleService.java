package faang.school.projectservice.service;

import faang.school.projectservice.jpa.ScheduleRepository;
import faang.school.projectservice.model.Schedule;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Schedule with ID: %s not found", id)));
    }
}
