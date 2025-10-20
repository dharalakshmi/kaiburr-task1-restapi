 How to run (step-by-step)

> These commands were used when preparing the submission and are safe to follow on Windows with Docker Desktop installed.

1. Start MongoDB (Docker)
```powershell
docker run -d --name kaiburr-mongo -p 27017:27017 -v kaiburr-mongo-data:/data/db mongo:6
docker ps --filter "name=kaiburr-mongo"
2.	Build the project
(If you already have target/*.jar, skip to running the jar)
# If mvn isn't in this PowerShell session, set it for the session:
$env:MAVEN_HOME = "C:\path\to\apache-maven-3.9.x"
$env:PATH = $env:PATH + ";" + $env:MAVEN_HOME + "\bin"

mvn clean package -DskipTests
3.	Run the Spring Boot app (example uses port 8081 to avoid conflicts)
cd C:\Users\<youruser>\projects\kaiburr-task1-restapi
java -jar .\target\kaiburr-task1-restapi-0.0.1-SNAPSHOT.jar --server.port=8081
4.	API base URL
http://localhost:8081
Method	Path	Description
GET	/tasks	List all tasks
GET	/tasks/{id}	Get task by id
PUT	/tasks	Create/Update task (JSON body)
DELETE	/tasks/{id}	Delete task
GET	/tasks/search?name=...	Search tasks by name
PUT	/tasks/{id}/execute	Execute task command (safe validation)
Task JSON sample
{
  "id": "task-1",
  "name": "Print Hello",
  "owner": "Dharalakshmi",
  "command": "echo Hello Kaiburr!"
}
________________________________________
Commands used for testing (PowerShell)
Use PowerShell or curl.exe on Windows. Replace localhost:8081 if you used another port.
Create task
Invoke-WebRequest -Uri "http://localhost:8081/tasks" -Method PUT -Headers @{ "Content-Type" = "application/json" } -Body '{"id":"task-1","name":"Print Hello","owner":"Dharalakshmi","command":"echo Hello Kaiburr!"}'
List all tasks
curl.exe http://localhost:8081/tasks
Get single task
curl.exe http://localhost:8081/tasks/task-1
Execute task
Invoke-WebRequest -Uri "http://localhost:8081/tasks/task-1/execute" -Method PUT -Headers @{ "Content-Type" = "application/json" } -Body '{}'
Verify persisted data in Mongo (inside container)
docker exec kaiburr-mongo mongosh --quiet --eval "db.getSiblingDB('kaiburrdb').tasks.find().toArray


Notes on security / validation
•	The service uses a conservative whitelist + blacklist approach to validate commands. Allowed commands are restricted (e.g., echo, date, whoami, ls/dir, pwd, cat), and destructive commands are blocked.
•	For Kaiburr Task 2 (Kubernetes), the execution will be migrated to ephemeral BusyBox pods created programmatically (safer for multi-tenant environments).
________________________________________
How I validated (examples)
•	Created task with owner Dharalakshmi and command echo Hello Kaiburr!.
•	Executed the task and confirmed taskExecutions[0].output contains Hello Kaiburr! and ExitCode: 0.
•	Confirmed persistence via mongosh inside Docker.
________________________________________

