# Usamos una imagen ligera de Java 17 o 21 (según lo que eligieras)
FROM eclipse-temurin:21-jdk-alpine

# Creamos un directorio de trabajo
WORKDIR /app

# Copiamos el archivo .jar generado por Maven (asegúrate de hacer mvn package primero)
# El nombre 'api-0.0.1-SNAPSHOT.jar' depende de tu pom.xml (artifactId + version)
COPY target/api-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]