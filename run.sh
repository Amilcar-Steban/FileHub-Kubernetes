minikube addons enable ingress
kubectl apply -f configmaps/
kubectl apply -f deployments/
kubectl apply -f services/
kubectl apply -f volumes/

#Delete all
#kubectl delete all -n ingress-nginx --all
#kubectl delete all --all