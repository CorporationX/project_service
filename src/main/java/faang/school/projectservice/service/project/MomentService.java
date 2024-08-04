//package faang.school.projectservice.service.project;
//
//import faang.school.projectservice.jpa.ProjectJpaRepository;
//import faang.school.projectservice.model.Moment;
//import faang.school.projectservice.model.Project;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class MomentService {
//    private final ProjectJpaRepository projectJpaRepository;
//
//    public void addMomentToAccomplishedProject(Project project, List<Moment> moments, Long userId) {
//        List<Moment> momentsOfCompletedProjects;
//        if (moments.isEmpty()) {
//            momentsOfCompletedProjects = addMomentToList(project.getId(), new ArrayList<>(), userId);
//        } else {
//            momentsOfCompletedProjects = addMomentToList(project.getId(), moments, userId);
//        }
//        project.setMoments(momentsOfCompletedProjects);
//        log.info("Moment was set successfully for project id = {}", project.getId());
//        projectJpaRepository.save(project);
//    }
//
//    private List<Moment> addMomentToList(long id, List<Moment> moments, long userId) {
//        Moment moment = Moment.builder()
//                .name("project " + id + " has been completed")
//                .updatedBy(userId)
//                .createdBy(userId)
//                .build();
//        List<Moment> list = new ArrayList<>(moments);
//        list.add(moment);
//        return list;
//    }
//}
