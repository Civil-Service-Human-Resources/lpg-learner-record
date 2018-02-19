FROM java:8

ENV SPRING_PROFILES_ACTIVE default
ENV XAPI_AUTHORISATION NjZmMmI0ZmMwMDFlM2RhOTkyZDIzYjU3ZDhhNzQ1NzY1NWJlYTA3ODoxYzBlMWI2ODI3NjA2ZDdlZmVkNzFlMjA0OTM5ZDA0OGY5NGY4NDJi
ENV XAPI_URL http://localhost:8083/data/xAPI

EXPOSE 9000

CMD java -Dxapi.url="${XAPI_URL}" -Dxapi.authorisation="${XAPI_AUTHORISATION}" -jar /data/app.jar

ADD build/libs/learner-record.jar /data/app.jar
