FROM dperson/samba:latest

# Copia tu archivo de configuración de Samba personalizado
COPY smb.conf /etc/samba/smb.conf

# Crea el directorio de datos si no existe
RUN mkdir -p /home/steb/distribuidos/data

# Expone los puertos para los servicios de Samba
EXPOSE 139 445

# Comando para ejecutar Samba
CMD ["smbd", "--foreground", "--no-process-group"]
