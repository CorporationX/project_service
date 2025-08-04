package faang.school.projectservice.delegate;

import faang.school.projectservice.api.InternshipsApiDelegate;
import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.service.internshi.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipApiImpl implements InternshipsApiDelegate {

    private final InternshipService internshipService;

//    @Override
//    public ResponseEntity<List<InternshipDto>> internshipsGet() {
//        return ResponseEntity.ok(internshipService.findAll());
//    }

    @Override
    public ResponseEntity<InternshipDto> internshipsIdGet(Integer id) {
//      return ResponseEntity.of(internshipService.findById(id.longValue()));
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(id);
        return ResponseEntity.ok(internshipDto);
    }

//    @Override
//    public ResponseEntity<Void> internshipsIdPut(Integer id, InternshipDto internshipDto) {
//        internshipService.updateInternship(id.longValue(), internshipDto);
//        return ResponseEntity.ok().build();
//    }
}