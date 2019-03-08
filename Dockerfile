FROM oracle/graalvm-ce:1.0.0-rc11
COPY installPackages.R installPackages.R
RUN yum install -y bzip2 && gu install r && Rscript installPackages.R
CMD java -version
#EXPOSE 8080
#COPY framework-0.0.1-SNAPSHOT.jar icb.jar
#ENTRYPOINT ["java","-jar","/icb.jar"]


#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#EXPOSE 8080
#ARG JAR_FILE=/framework-0.0.1-SNAPSHOT.jar
#ADD ${JAR_FILE} icodebetter.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/icodebetter.jar"]mvn install -Dmaven.test.skip=true