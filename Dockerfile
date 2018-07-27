FROM java:8

ENV SPRING_PROFILES_ACTIVE production

EXPOSE 9000

CMD java -jar /data/app.jar

ADD https://github.com/Civil-Service-Human-Resources/lpg-terraform-paas/releases/download/hammer-0.1/hammer /bin/hammer
RUN chmod +x /bin/hammer

ADD build/libs/learner-record.jar /data/app.jar
