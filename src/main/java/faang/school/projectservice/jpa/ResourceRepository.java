package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByIdAndProjectId(long id, long projectId);

    List<Resource> findAllByProjectId(long projectId);
}
