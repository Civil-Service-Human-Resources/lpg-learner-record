FROM java:8

ENV SPRING_PROFILES_ACTIVE production

ENV AUTH_USER user
ENV AUTH_PASSWORD password

ENV XAPI_USERNAME 66f2b4fc001e3da992d23b57d8a7457655bea078
ENV XAPI_PASSWORD 1c0e1b6827606d7efed71e204939d048f94f842b
ENV XAPI_URL http://localhost:8083/data/xAPI

EXPOSE 9000

CMD java -jar /data/app.jar

ADD https://github.com/Civil-Service-Human-Resources/lpg-terraform-paas/releases/download/hammer-0.1/hammer /bin/hammer
RUN chmod +x /bin/hammer

ADD build/libs/learner-record.jar /data/app.jar
