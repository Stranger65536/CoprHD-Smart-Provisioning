FROM hazelcast/hazelcast:3.9.3

ARG NODE

ADD ${NODE}/hazelcast.xml ${HZ_HOME}

ENV JAVA_OPTS="-Dhazelcast.partition.count=3"

CMD ./server.sh