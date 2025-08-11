package faang.school.projectservice.repository;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

import static faang.school.projectservice.specification.StageSpecification.buildSpecification;

public interface StageRepository extends JpaRepository<Stage, Long>, JpaSpecificationExecutor<Stage> {
    default List<Stage> findByFilter(StageFilterDto filterDto) {
        Specification<Stage> filter = buildSpecification(filterDto);
        return findAll(filter);
    }
}
