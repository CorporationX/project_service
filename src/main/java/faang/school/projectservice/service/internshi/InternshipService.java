package faang.school.projectservice.service.internshi;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipStatus;
import faang.school.projectservice.apimodel.TeamRole;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * intershipService — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author agent
 * @since 04.08.2025
 */
public interface InternshipService {

    InternshipDto createInternship(Long projectId, InternshipDto dto);

    InternshipDto updateInternship(Long id, InternshipDto dto);

    List<InternshipDto> findAll();

    Optional<InternshipDto> findById(Long id);

    List<InternshipDto> findByProject(Long projectId, @Nullable InternshipStatus status, @Nullable TeamRole role);
}