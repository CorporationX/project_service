package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/moments")
public class MomentController {

    private MomentService momentService;

    @Autowired
    public void setMomentService(MomentService momentService) {
        this.momentService = momentService;
    }

    @PostMapping
    public MomentDto createMoment(@Valid @RequestBody MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping
    public MomentDto updateMoment(@Valid @RequestBody MomentDto momentDto) {
        return momentService.updateMoment(momentDto);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{id}")
    public MomentDto getMomentById(@PathVariable long id) {
        return momentService.getMomentById(id);
    }

    @GetMapping("/filter")
    public List<MomentDto> getMomentsByFilters(@RequestParam(required = false) Date date,
                                               @RequestParam(required = false) List<Long> partnerProjectIds) {
        return momentService.getMomentsByFilters(date, partnerProjectIds);
    }
}