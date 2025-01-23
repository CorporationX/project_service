package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleRepositoryAdapter {
    private final ScheduleRepository scheduleRepository;

    public Schedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Schedule with id: %s not found!", id)));
    }
}
