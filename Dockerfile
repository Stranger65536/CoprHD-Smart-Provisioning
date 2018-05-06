FROM openjdk:8u162-jdk

ARG VERSION
ENV NODE=${NODE}

COPY config config
COPY templates templates
COPY static static
COPY resources resources
COPY build/libs/CoprHDSM-${VERSION}.jar CoprHDSM.jar

ENTRYPOINT exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
                     -Dspring.config.location=config,config/docker-node-${NODE}.yml \
                     -Dhazelcast.diagnostics.enabled=true \
                     -Dspring.profiles.active=default \
                     -jar CoprHDSM.jar

