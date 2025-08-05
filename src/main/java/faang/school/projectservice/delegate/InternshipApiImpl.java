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

    @Override
    public ResponseEntity<List<InternshipDto>> internshipsGet() {
        List<InternshipDto> internships = internshipService.findAll();
        return ResponseEntity.ok(internships);
    }

    @Override
    public ResponseEntity<InternshipDto> internshipsIdGet(Integer id) {
        InternshipDto internshipDto = internshipService.findById(Long.valueOf(id));
        return ResponseEntity.ok(internshipDto);
    }

    @Override
    public ResponseEntity<Void> internshipsIdPut(Integer id, InternshipDto internshipDto) {
        internshipService.update(Long.valueOf(id), internshipDto);
        return ResponseEntity.noContent().build();
    }

}