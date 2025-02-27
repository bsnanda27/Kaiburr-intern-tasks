
# Task 2. Kubernetes

This part involves containerizing task management app with Docker and deploying it to Kubernetes. The application dynamically executes commands by creating Kubernetes pods using the BusyBox image. A Persistent Volume (PV) is used for MongoDB to ensure consistency.




## Kubernetes Deployment

### Cluster Setup

Kubernetes cluster is set up using docker-desktop

![Task 2](SCREENSHOTS/Task2-ClusterSetup.png)

### Containerizing the Application
```bash
docker build -t myrepo/task-manager:latest .
docker push myrepo/task-manager:latest
```
![Task 2](SCREENSHOTS/Task2-Docker.png)

### Kubernetes Manifests

The following Kubernetes objects are used:

- Task Deployment: Runs the Task Manager API

- Task Service: Exposes the API (Type: NodePort)

- MongoDB Deployment & Service: Runs MongoDB and exposes it internally

- MongoDB Persistent Volume (PV): Ensures MongoDB data persists


```bash
kubectl apply -f mongodb-pv.yaml
kubectl apply -f mongodb-service.yaml
kubectl apply -f mongodb-deployment.yaml
kubectl apply -f task-api-deployment.yaml
```
![Task 2](SCREENSHOTS/Task2-Deploy.png)
This screenshot shows the `task-api-service` and `mongodb-service` are running in Kubernetes. Their respective pods also running.











## Screenshots

### API Testing Using Postman
#### Get All Tasks
![Task 2](SCREENSHOTS/Task2-ApiTesting.png)
#### Execute Task By ID 
![Task 2](SCREENSHOTS/Task2-ExecuteTask.png)

Here I have used `tasks/{id}/executekub` which creates a BusyBox pod dynamically , executes the shell command inside the BusyBox pod and retireves the `logs` (output).

### BusyBox Execution
A BusyBox pod is dynamically created to execute each task instead of running commands locally. The task command runs inside the BusyBox pod, and logs are retrieved and stored in MongoDB.


```bash
kubectl get events --sort-by=.metadata.creationTimestamp | tail -n 6
```
![Task 2](SCREENSHOTS/Task2-BusyBox.png)

###MongoDB Data Persistence (Persistent Volume - PV)

A Persistent Volume (PV) ensures MongoDB data persists even after the pod is deleted.

```bash
kubectl get pod
kubectl exec -it <mongo-db-pod> -- mongosh "mongodb://admin:password@mongodb-service:27017/taskdb?authSource=admin"
db.tasks.find().pretty()
```
![Task 2](SCREENSHOTS/Task2-PVC0.png)

![Task 2](SCREENSHOTS/Task2-PVC1.png)

## Conclusion

The Task Manager app was successfully containerized and deployed on Kubernetes. BusyBox pods dynamically execute tasks, and MongoDB data persists even after pod deletion using Persistent Volumes.

