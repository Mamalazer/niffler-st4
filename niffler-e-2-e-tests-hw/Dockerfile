ARG DOCKER
FROM $DOCKER
ENV JAVA_OPTS=""

WORKDIR /niffler
COPY ./gradle ./gradle
COPY ./gradlew ./
COPY ./niffler-e-2-e-tests-hw ./niffler-e-2-e-tests-hw
COPY ./niffler-grpc-common ./niffler-grpc-common
COPY ./build.gradle ./
COPY ./settings.gradle ./

CMD chmod +x ./niffler-e-2-e-tests-hw/send_results.sh; ./gradlew test -Dtest.env=docker -Drepository=jpa; bash ./niffler-e-2-e-tests-hw/send_results.sh
