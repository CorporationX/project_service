package faang.school.projectservice.repository;

import faang.school.projectservice.model.Vacancy;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    @Override
    <S extends Vacancy> S save(S entity);

    @Override
    Optional<Vacancy> findById(Long aLong);

    @Override
    <S extends Vacancy> List<S> findAll(Example<S> example);

    @Override
    void deleteById(Long aLong);
}
