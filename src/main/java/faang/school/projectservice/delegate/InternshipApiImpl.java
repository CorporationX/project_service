package faang.school.projectservice.delegate;

import faang.school.projectservice.api.InternshipsApiDelegate;
import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipApiImpl implements InternshipsApiDelegate {

    private final InternshipService internshipService;

    /**
     * GET /internships :
     * Получить список всех стажировок.
     *
     * @return список объектов {@link InternshipDto}
     */
    @Override
    public ResponseEntity<List<InternshipDto>> internshipsGet() {
        List<InternshipDto> internships = internshipService.findAll();
        return ResponseEntity.ok(internships);
    }

    /**
     * GET /internships/{id} :
     * Получить стажировку по идентификатору.
     *
     * @param id идентификатор стажировки
     * @return объект {@link InternshipDto}
     */
    @Override
    public ResponseEntity<InternshipDto> internshipsIdGet(Integer id) {
        InternshipDto internshipDto = internshipService.findById(Long.valueOf(id));
        return ResponseEntity.ok(internshipDto);
    }

    /**
     * PUT /internships/{id} :
     * Обновить или завершить стажировку.
     *
     * @param id идентификатор стажировки
     * @param internshipDto данные для обновления
     * @return пустой ответ с кодом 204 No Content
     */
    @Override
    public ResponseEntity<Void> internshipsIdPut(Integer id, InternshipDto internshipDto) {
        internshipService.update(Long.valueOf(id), internshipDto);
        return ResponseEntity.noContent().build();
    }

}