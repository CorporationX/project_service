rootProject.name = "ProjectService"
include("src:test:java")
findProject(":src:test:java")?.name = "java"
include("src:test:java:src:test:java")
findProject(":src:test:java:src:test:java")?.name = "java"
