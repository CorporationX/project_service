package faang.school.projectservice.repository;

import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Vacancy, Long> {
}
