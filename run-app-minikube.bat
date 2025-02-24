
 minikube start --driver=docker
 REM kompose convert -o ./kubernetes
  REM echo Building Java Spring Boot application...
  REM docker build --build-arg API_KEY=%API_KEY% --build-arg FILTER_INTERVAL=%FILTER_INTERVAL% --build-arg API_URL=%API_URL% -t backend:latest -f Dockerfile .

 REM cd frontend

REM echo Building Angular frontend application...
  REM docker build --build-arg API_KEY=%API_KEY% --build-arg FILTER_INTERVAL=%FILTER_INTERVAL% --build-arg API_URL=%API_URL% -t frontend:latest -f Dockerfile .

REM cd ..
  REM cd integration-developer-master

REM echo Building Music App application...
  REM docker build --build-arg -t media-app:latest -f Dockerfile .
  REM echo Build process completed.

  REM cd ..
  REM cd keycloak

REM echo Building Keycloak application...
  REM docker build -t mykeycloak:latest -f Dockerfile .

 REM cd ..
 REM cd kubernetes
 REM minikube image load frontend:latest
 REM minikube image load music-app:latest
 REM minikube image load backend:latest
 REM minikube image load mykeycloak:latest
 REM kubectl apply -f .

REM This batch file sets up port forwarding for all services


REM Port forward frontend service
REM start "" cmd /k "kubectl port-forward svc/frontend 4200:4000"

REM pause