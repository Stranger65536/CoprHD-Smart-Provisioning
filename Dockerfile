FROM openjdk:8u162-jdk

ARG VERSION

COPY config config
COPY build/libs/CoprHDSM-${VERSION}.jar CoprHDSM.jar

ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
               -Dspring.config.location=config \
               -Dspring.profiles.active=default"

ENTRYPOINT exec java ${JAVA_OPTS} -jar CoprHDSM.jar

