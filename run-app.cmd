@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.3"
set "MAVEN_USER_HOME=C:\Users\sumit\Documents\Codex\2026-04-28\build-a-simple-student-university-crud\.m2"
call mvnw.cmd spring-boot:run > app.log 2> app.err.log
