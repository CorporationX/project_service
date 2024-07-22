package faang.school.projectservice.service;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;

    public Moment createMoment(Initiative initiative) {
        Moment moment = Moment.builder()
                .name(initiative.getName())
                .description(initiative.getDescription())
                .date(LocalDateTime.now())
                .projects(initiative.getSharingProjects())
                .createdAt(LocalDateTime.now())
                .userIds(List.of(initiative.getCurator().getId()))
                .build();
        momentRepository.save(moment);
        return moment;
    }
}
