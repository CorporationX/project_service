package faang.school.projectservice.jpa;

import faang.school.projectservice.model.entity.Meet;
import faang.school.projectservice.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long>, JpaSpecificationExecutor<Meet> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    @Query("select m from Meet m order by m.id limit :limit offset :offset")
    List<Meet> findAllWithLimitAndOffset(int limit, int offset);
}
