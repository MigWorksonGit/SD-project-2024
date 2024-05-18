How to run:

download maven from https://maven.apache.org/

execute the command: mvn clean compile assembly:single

To run Meta1 folder classes:
java -cp target/project-1.0.jar project.Meta1.<class_name>

Example:
java -cp target/project-1.0.jar project.Meta1.GatewayServer
java -cp target/project-1.0.jar project.Meta1.Barrel
java -cp target/project-1.0.jar project.Meta1.Downloader
java -cp target/project-1.0.jar project.Meta1.Client

To run Meta 2 (frontend):
mvn spring-boot:run