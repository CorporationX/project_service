package faang.school.projectservice.listeners;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.listeners.events.MomentEvent;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentsListener {
    private final MomentService momentService;

    @EventListener
    public void handleMomentsListener(MomentEvent me) {
        MomentRequestDto momentRequestDto = new MomentRequestDto();
        momentRequestDto.setName(me.getMessage());
        List<Long> completedChildrenProjectsIds = me.getChildProjects().stream().map(Project::getId).toList();
        //        completedProjects.add(me.getProject().getId());
        List<Long> completedProjects = new ArrayList<>(completedChildrenProjectsIds);
        momentRequestDto.setProjectIds(completedProjects);
//        momentRequestDto.setDescription(me.getMessage());
        momentService.addNew(momentRequestDto, me.getUpdatedBy());
//        momentService.addMomentToAccomplishedProject(me.getProject(), me.getMoments(), me.getUpdatedBy());
    }
}
