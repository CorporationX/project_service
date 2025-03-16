rootProject.name = "ProjectService"
include("src:test:faang.school.projectservice")
findProject(":src:test:faang.school.projectservice")?.name = "faang.school.projectservice"
