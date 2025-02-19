# 1. 사용할 기본 이미지 설정 (OpenJDK 17)
FROM openjdk:17-jdk-slim

# 2. 컨테이너에서 작업할 디렉토리 생성
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 4. Spring Boot가 실행될 포트 지정
EXPOSE 8080

# 5. 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]