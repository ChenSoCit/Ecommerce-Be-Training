#image nền (base image) cho container.
FROM eclipse-temurin:17-jdk-jammy

#JAR_FILE trỏ đến file .jar trong thư mục target/
ARG JAR_FILE=target/*.jar

#Copy file .jar từ máy host vào container
COPY ${JAR_FILE} app.jar

#Chỉ định lệnh mặc định khi container start
ENTRYPOINT ["java", "-jar", "app.jar"]

#Khai báo container lắng nghe cổng 8080
EXPOSE 8080
