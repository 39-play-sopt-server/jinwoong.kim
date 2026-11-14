# Java21실행 환경을 기반으로 사용
FROM eclipse-temurin:21-jre-jammy
# 컨테이너 내부 작업 폴더 지정
WORKDIR /app
# 맥에서 빌드한 JAR을 이미지 내부에 app.jar로 복사
COPY build/libs/assignment-1.0-SNAPSHOT.jar app.jar
# 앱이 8080번을 사용한다는 정보 표시. 외부 공개는 별도 설정
EXPOSE 8080
# 컨테이너 시작시 Spring을 prod프로필로 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]