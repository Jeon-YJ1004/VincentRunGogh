# 토대가 되는 이미지 선택. 자바 런타임 환경 선택.
FROM openjdk:17-jdk

# jar file 변수로 정의하기
ARG JAR_FILE=./build/libs/*.jar

# jar file 상대경로 - 컨테이너화 시점 내부 경로(메인 디렉토리)
COPY ${JAR_FILE} app.jar

# 시스템 진입점 강제 정의 및 자동 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 컨테이너 내부 감시 포트(노출 포트)
EXPOSE 8080