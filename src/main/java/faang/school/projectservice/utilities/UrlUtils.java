package faang.school.projectservice.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlUtils {
        public static final String MAIN_URL = "/api/project-service";
        public static final String V1 = "/v1";
        public static final String PROJECTS = "/projects";
        public static final String FILTER = "/filter";
        public static final String ID = "/{id}";

        public static final String VACANCY = "/vacancy";
        public static final String VACANCY_FILTER = "/filter";
        public static final String VACANCY_ID = "/id";
  
        public static final String PROJECT_COVER = "/project-cover";
        public static final String PROJECT_COVER_ID = "/{projectId}";
}