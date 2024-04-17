FROM openjdk:11-jdk-slim

# Instalar samba-client y otras dependencias
RUN apt-get update && apt-get install -y samba-client openssl curl


# Crear directorio para la aplicación
WORKDIR /app

# Copiar los archivos del proyecto al contenedor
COPY target/*.jar app-1.0-SNAPSHOT-jar-with-dependencies.jar
#COPY consul.sh consul.sh

# Dar permisos de ejecución al script de registro de Consul
#RUN chmod +x consul.sh

# Exponer el puerto en el que se ejecuta la aplicación
EXPOSE 4567

# Comando para ejecutar el script de registro de Consul y luego la aplicación Java
CMD ["java", "-jar", "app-1.0-SNAPSHOT-jar-with-dependencies.jar"]
