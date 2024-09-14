package faang.school.projectservice.controller;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/moments")
public class MomentController {

    @Autowired
    private MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping
    public MomentDto updateMoment(@RequestBody MomentDto momentDto) {
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
