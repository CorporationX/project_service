package faang.school.projectservice.repository;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MomentRepository extends JpaRepository<Moment, Long> {

    void save(MomentDto moment);

    Optional<Moment> findById(long id);

    List<Moment> findAll();

    void deleteById(long id);

    @Query(nativeQuery = true, value = """
    select m.* from moment m
    where m.id in
    (select moment_id from moment_project where project_id = :projectId)
    """)
    List<Moment> findAllByProjectId(long projectId);
}
