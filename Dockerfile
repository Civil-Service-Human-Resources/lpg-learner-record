FROM java:9

ENV SPRING_PROFILES_ACTIVE default

EXPOSE 9000

CMD java -jar /data/app.jar

ADD build/libs/learner-record.jar /data/app.jar
