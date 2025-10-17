package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.SearchMomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping("/moments/newmoment")
    public MomentDto createMoment(@RequestBody @NonNull CreateMomentDto momentDto) throws Exception {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/moments/updating")
    public MomentDto updateMoment(
            @RequestParam("id") long momentId, @RequestBody @NonNull UpdateMomentDto momentDto) throws Exception {
        return momentService.updateMoment(momentId, momentDto);
    }

    @GetMapping("/moments")
    public MomentDto getById(@RequestParam("id") long momentId) {
        return momentService.getById(momentId);
    }

    @GetMapping("/moments")
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/moments/byproject")
    public List<MomentDto> getMomentsByProjectId(@RequestParam("id") long projectId) {
        return momentService.getMomentsByProjectId(projectId);
    }

    @PostMapping("/moments/bymonth")
    public List<MomentDto> getMomentsByMonth(@RequestBody SearchMomentDto searchMomentDto) {
        return momentService.getMomentsByMonth(searchMomentDto);
    }

    @DeleteMapping("/moments")
    public void deleteById(@RequestParam("id") long momentId){
        momentService.deleteById(momentId);
    }
}
