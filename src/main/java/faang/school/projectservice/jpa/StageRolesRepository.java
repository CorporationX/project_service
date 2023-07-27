package faang.school.projectservice.jpa;

import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageRolesRepository extends JpaRepository<StageRoles, Long> {
}
