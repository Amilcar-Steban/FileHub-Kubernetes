# Parcial 1 - Sistemas distribuidos
- Por:
- Amilcar Steban Rodriguez - A00369769
- Samuel Guerrero - A00365567

## Parametros para parcial
1. Java para el Backend
2. SAMBA como almacenamiento centralizado
3. Nginx como server para el front
4. Spark para los CORS
5. Redis para la base de datos del ApiGateway
6. Consul
7. HAproxy para el balanceador de cargas
# Respuesta
Para cumplir con los requisitos, la plataforma debe asegurar la seguridad, implementar monitoreo y registro, ser escalable, contar con documentación detallada y pasar por pruebas exhaustivas. Consideraría agregar características como búsqueda avanzada, análisis de datos, integración con CMS, automatización de tareas, soporte multiidioma e integración con servicios externos.

# Como ejecutar
## 1. Instalación de Docker Compose en WSL

1. **Instalar Docker en Windows**: Asegúrate de tener Docker instalado y funcionando en tu sistema Windows. Puedes descargar e instalar Docker Desktop desde el sitio web oficial de Docker.

2. **Habilitar WSL**: Asegúrate de tener WSL habilitado en tu sistema Windows. Puedes habilitarlo desde la configuración de Windows.

3. **Instalar una Distribución de Linux en WSL**: Después de habilitar WSL, necesitarás instalar una distribución de Linux en WSL. Puedes hacerlo desde la Microsoft Store o utilizando el comando `wsl --install`.

4. **Abrir la Distribución de Linux**: Abre la distribución de Linux instalada en WSL desde el menú de inicio o ejecutando el comando `wsl` desde PowerShell o la línea de comandos de Windows.

5. **Actualizar el Sistema de Linux**: Ejecuta los siguientes comandos en la distribución de Linux para asegurarte de que todos los paquetes estén actualizados:
   ```bash
   sudo apt update
   sudo apt upgrade
6. **Instalar Docker Compose**: Una vez en la distribución de Linux, puedes instalar Docker Compose ejecutando los siguientes comandos:
   ```bash
   sudo apt install curl
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   sudo usermod -aG docker $USER
7. **Verificar la Instalación**: Después de instalar Docker, verifica que esté funcionando correctamente ejecutando el siguiente comando:
   ```bash
   docker --version
   docker-compose --version

## 2. Descargar proyecto git
``git clone https://github.com/Amilcar-Steban/ds-exams.git``
## 3. Ejecutar proyecto
1. Abre WSL y dirigte al directorio del proyecto recien descargado
2. Dentro del directorio vas a ejecutar el siguiente comando
   ```bash
   docker compose up --build -d
3. Despues vas a verificar que el stack este corrinedo dentro del docker desktop

# Documentacion
## Samba
Samba es una implementación libre del protocolo de archivos compartidos de Windows que permite a los sistemas Unix/Linux compartir archivos e impresoras con los sistemas Windows. En este documento, se proporciona una guía para configurar un servidor Samba utilizando Docker.
## Dockerfile del servidor Samba
   ```Dockerfile
   FROM dperson/samba:latest

   #Copia el archivo de configuración personalizado de Samba
   COPY smb.conf /etc/samba/smb.conf

   #Crea el directorio de datos si no existe
   RUN mkdir -p /home/steb/distribuidos/data

   #Expone los puertos para los servicios de Samba
   EXPOSE 139 445

   #Comando para ejecutar Samba
   CMD ["smbd", "--foreground", "--no-process-group"]
```

El archivo `smb.conf` contiene la configuración del servidor Samba. Define parámetros como el grupo de trabajo, el nombre del servidor, y las opciones de seguridad y compartición de archivos. Esta configuración permite personalizar el comportamiento y las funcionalidades del servidor Samba según las necesidades específicas del entorno.

### Configuracion
1. El archivo `smb.conf` contiene la configuración global de Samba, así como la definición de compartición de recursos.
2. Se define un recurso compartido llamado storage_samba que permite a los usuarios autenticados acceder y escribir en el directorio /home/steb/distribuidos/data/.

## Frontend
El Frontend se compone de un HTML y un CSS basico, que se encarga principalmente cargar los archivos PDF y entregarselos al backend y ademas de realizar la función de eliminar dichos archivos. Para este Front usamos nginx como servidor para subir la app y usamos sus configuraciones para poder realizar peticiones al backend. Estos son los comandos para crear el front:

```
sudo docker build -t frontend:0.1.0 .
sudo docker run -d -p 8000:80 --network microparcial --name front-app frontend:0.1.0

sudo docker build -t frontend2:0.1.0 .
sudo docker run -d -p 8001:80 --network microparcial --name front-app2 frontend2:0.1.0
```



## Backend
El Backend esta programado en Java, utiliza spark para manejar los CORS y la librería SMB para poder realizar peticiones a SAMBA que es el almacenamiento centralizado que estamos usando para este proyecto.

Las funciones del backend en este caso son /upload /delete /get_list_files que son los requerimientos básicos para que funcione correctamente una app de almacenamiento. Estos son los comandos necesarios para crear el jar del programa back y para ejecutar dos instancias del backend.

```
   commands backend:

mvn clean compile assembly:single //ejecutar en app/ para generar el jar
sudo docker build -t backendapp:0.1.0 .
sudo docker run -d -p 4567:4567 --network microparcial --name backendsmb backendapp:0.1.0
sudo docker build -t backendapp2:0.1.0 .
sudo docker run -d -p 4568:4567 --network microparcial --name backendsmb2 backendapp2:0.1.0


```

## Consul 
Consul es una herramienta que facilita la gestión de servicios en entornos distribuidos. Permite registrar, descubrir y monitorear servicios de manera automática, simplificando la implementación y escalabilidad de aplicaciones distribuidas. Además, ofrece capacidades avanzadas de enrutamiento de servicios para mejorar la resiliencia y eficiencia de las aplicaciones. Estos son los comandos para crear el contenedor del consul:

```
docker run -d -p 8500:8500 -p 8600:8600/udp --network microparcial --name consul consul:1.15 agent -server -bootstrap-expect 1 -ui -data-dir /tmp -client=0.0.0.0
```

## Api Gateway
Básicamente recibe las peticiones url y las comparte al load balancer segun su tipo, por ejemplo la ruta "/" redirige al front de la app. La configuración de este Api simplemente añade los endpoint y les da unas caracteristicas para que funcionen correctamente.

Los comandos para que funcione el APIGateway son los siguientes:
```
Crear la imagen:
docker build -t express-gw:0.1.0 .

Crear la base de datos redis:
docker run --network microparcial -d --name redis_data -p 6379:6379 redis:alpine

correr el contenedor Gateway
docker run -d --name express-gateway --network microparcial -v $(pwd):/var/lib/eg -p 8080:8080 -p 5555:9876 express-gateway
```
Este crea un volumen docker en la ruta actual/var/lib/eg y usa los puertos 8080 y 9876 paraa redirigir las peticiones y editar las configuraciones del usuario admin (opcional)
