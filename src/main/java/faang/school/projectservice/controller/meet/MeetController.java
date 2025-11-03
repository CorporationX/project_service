package faang.school.projectservice.controller.meet;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.service.meet.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/meets")
public class MeetController {
    private final MeetService meetService;

    @PostMapping
    public ResponseEntity<MeetDto> create(@RequestBody @Valid CreateMeetDto createMeetDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meetService.create(createMeetDto));
    }

    @PatchMapping("/{meetId}")
    public ResponseEntity<MeetDto> update(@PathVariable long meetId, @RequestBody @Valid UpdateMeetDto updateMeetDto) {
        return ResponseEntity.ok(meetService.update(meetId, updateMeetDto));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MeetDto>> getByFilters(@ModelAttribute MeetFilterDto meetFilterDto) {
        return ResponseEntity.ok(meetService.getByFilters(meetFilterDto));
    }

    @GetMapping("/{meetId}")
    public ResponseEntity<MeetDto> getById(@PathVariable long meetId) {
        return ResponseEntity.ok(meetService.getById(meetId));
    }

    @GetMapping
    public ResponseEntity<List<MeetDto>> getAll() {
        return ResponseEntity.ok(meetService.getAll());
    }

    @DeleteMapping("/{meetId}")
    public ResponseEntity<Void> delete(@PathVariable long meetId) {
        meetService.delete(meetId);
        return ResponseEntity.ok().build();
    }
}