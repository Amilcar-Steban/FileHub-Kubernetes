#!/bin/bash

#Crear network
#docker network create microparcial

# Comandos para Samba
cd samba
docker build -t samba:0.1.0 .
#sudo docker run -dit -p 139:139 -p 445:445 --name sambadb --network microparcial -v samba:/home/steb/distribuidos/data samba:0.1.0
cd ..

# Comandos para el backend
cd backend/app
mvn clean compile assembly:single
sudo docker build -t backendapp:0.1.0 .
#sudo docker run -d -p 4567:4567 --network microparcial --name backendsmb backendapp:0.1.0
#sudo docker build -t backendapp2:0.1.0 .
#sudo docker run -d -p 4568:4567 --network microparcial --name backendsmb2 backendapp2:0.1.0

cd ..
cd ..

# Comandos para el frontend
cd frontend
sudo docker build -t frontend:0.1.0 .
#sudo docker run -d -p 8000:80 --network microparcial --name front-app frontend:0.1.0
#sudo docker build -t frontend2:0.1.0 .
#sudo docker run -d -p 8001:80 --network microparcial --name front-app2 frontend2:0.1.0
#cd ..

# Comandos para Consul
#docker run -d -p 8500:8500 -p 8600:8600/udp --network microparcial --name consul consul:1.15 agent -server -bootstrap-expect 1 -ui -data-dir /tmp -client=0.0.0.0

# Comandos para Gateway
#cd apptg
#docker build -t express-gw:0.1.0 .
#docker run --network microparcial -d --name redis_data -p 6379:6379 redis:alpine
#docker run -d --name express-gateway --network microparcial -v $(pwd):/var/lib/eg -p 8080:8080 -p 5555:9876 express-gateway
#cd ..

# Comandos para Load Balancer
#cd loadbalancer
#docker build -t balancer:0.1.0 .
#docker run -d -p 5000:8080 -p 1936:1936 --network microparcial --name balancer balancer:0.1.0
#cd ..
