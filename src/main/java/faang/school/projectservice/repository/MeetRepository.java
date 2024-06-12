package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.MeetJpaRepository;
import faang.school.projectservice.model.Meet;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetRepository {
    private final MeetJpaRepository meetJpaRepository;

    public Meet findById(Long meetId) {
        return meetJpaRepository.findById(meetId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Meet not found by id: %s", meetId))
        );
    }
}
