#!/bin/sh

# Instala openssl y curl
apt-get update && apt-get install -y openssl curl

CONSUL_URL=${CONSUL_URL:-"http://consul:8500"}

# Función para obtener la dirección IP del contenedor
get_ip() {
  local ip_address=$(hostname -I 2>/dev/null)
  echo $ip_address
}

get_container_id() {
  local id_container=$(hostname)
  echo "$id_container"
}

# Función para registrar el servicio en Consul
register_service() {
    local consul_url="$1"
    local ip=$(get_ip)
    local random_id=$(openssl rand -hex 5)
    local service_name="Backend"
    local id_container=$(get_container_id)
    echo "IP DEL CONTENEDOR: '$ip'"
    payload='{
      "ID": "'"$service_name"'-HealthCheck - '$id_container'",
      "Name": "'"$service_name"'-'$id_container'",
      "Tags": ["back"],
      "Address": "'$ip'",
      "Port": 4567,
      "Check": {
        "DeregisterCriticalServiceAfter": "90m",
        "HTTP": "http://'$ip':4567/health",
        "Interval": "10s",
        "Timeout": "1s"
      }
    }'

    echo $payload

    curl -X PUT -H "Content-Type: application/json" --data "$payload" "$consul_url"
}

# Registra el servicio en Consul
register_service "$CONSUL_URL/v1/agent/service/register"

echo "Iniciando la aplicación Java..."
java -jar app-1.0-SNAPSHOT-jar-with-dependencies.jar
