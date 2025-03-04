FROM maven:3.8.6-amazoncorretto-17 as debug

WORKDIR /workspace/app

COPY . .

FROM debug as develop

RUN mvn clean package -Dmaven.test.skip=true

CMD java -jar /workspace/app/target/report-0.0.1-SNAPSHOT.jar

FROM amazoncorretto:17.0.5-alpine3.16 as production

ARG JAR_DIR=/workspace/app/target

COPY --from=develop ${JAR_DIR}/report-0.0.1-SNAPSHOT.jar /data/app.jar

# Add AppInsights config and agent jar
ADD lib/AI-Agent.xml /opt/appinsights/AI-Agent.xml
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.4/applicationinsights-agent-3.4.4.jar /opt/appinsights/applicationinsights-agent-3.4.4.jar

CMD java -javaagent:/opt/appinsights/applicationinsights-agent-3.4.4.jar -jar /data/app.jar
