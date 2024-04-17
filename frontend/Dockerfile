# Utiliza una imagen base de Nginx
FROM nginx:alpine

# Elimina la configuración predeterminada de Nginx
#RUN rm /etc/nginx/conf.d/default.conf
#RUN apk --no-cache add curl

# Copia la configuración personalizada de Nginx al contenedor
#COPY nginx.conf /etc/nginx/nginx.conf

# Copia el archivo index.html al contenedor
COPY index.html /usr/share/nginx/html/index.html
COPY styles.css /usr/share/nginx/html/

EXPOSE 80
# Set the entrypoint.sh script as executable
#RUN chmod +x /register-service.sh

# Specify the entrypoint script to run when the container starts
ENTRYPOINT ["nginx", "-g", "daemon off;"]