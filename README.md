# 🚀 Kaiburr Task 1 — REST API with Spring Boot and MongoDB

A simple Spring Boot REST API for task management, backed by MongoDB running in Docker.  
This guide walks you through setup, build, and testing steps on **Windows** using **PowerShell** and **Docker Desktop**.

------------------------------------------------------------
🧱 PREREQUISITES
------------------------------------------------------------
- Docker Desktop
- Java 17+
- Apache Maven 3.9.x+

------------------------------------------------------------
⚙️ STEP 1 — START MONGODB (DOCKER)
------------------------------------------------------------
docker run -d --name kaiburr-mongo -p 27017:27017 -v kaiburr-mongo-data:/data/db mongo:6
docker ps --filter "name=kaiburr-mongo"

------------------------------------------------------------
🏗️ STEP 2 — BUILD THE PROJECT
------------------------------------------------------------
# If you already have target/*.jar, skip to Step 3
# (Optional) Configure Maven for the current PowerShell session
$env:MAVEN_HOME = "C:\path\to\apache-maven-3.9.x"
$env:PATH = $env:PATH + ";" + $env:MAVEN_HOME + "\bin"

# Build the JAR
mvn clean package -DskipTests

------------------------------------------------------------
▶️ STEP 3 — RUN THE APPLICATION
------------------------------------------------------------
cd C:\Users\<youruser>\projects\kaiburr-task1-restapi
java -jar .\target\kaiburr-task1-restapi-0.0.1-SNAPSHOT.jar --server.port=8081

------------------------------------------------------------
🌐 STEP 4 — API BASE URL
------------------------------------------------------------
Base URL: http://localhost:8081

METHOD   | PATH                        | DESCRIPTION
---------|-----------------------------|--------------------------------------
GET      | /tasks                      | List all tasks
GET      | /tasks/{id}                 | Get task by ID
PUT      | /tasks                      | Create or update a task
DELETE   | /tasks/{id}                 | Delete a task
GET      | /tasks/search?name=...      | Search tasks by name
PUT      | /tasks/{id}/execute         | Execute task command (validated safely)

------------------------------------------------------------
🧾 TASK JSON SAMPLE
------------------------------------------------------------
{
  "id": "task-1",
  "name": "Print Hello",
  "owner": "Dharalakshmi",
  "command": "echo Hello Kaiburr!"
}

------------------------------------------------------------
🧪 STEP 5 — TESTING COMMANDS (POWERSHELL)
------------------------------------------------------------
# Create a Task
Invoke-WebRequest -Uri "http://localhost:8081/tasks" -Method PUT -Headers @{ "Content-Type" = "application/json" } -Body '{"id":"task-1","name":"Print Hello","owner":"Dharalakshmi","command":"echo Hello Kaiburr!"}'

# List All Tasks
curl.exe http://localhost:8081/tasks

# Get Single Task
curl.exe http://localhost:8081/tasks/task-1

# Execute Task
Invoke-WebRequest -Uri "http://localhost:8081/tasks/task-1/execute" -Method PUT -Headers @{ "Content-Type" = "application/json" } -Body '{}'

# Verify Persisted Data in MongoDB
docker exec kaiburr-mongo mongosh --quiet --eval "db.getSiblingDB('kaiburrdb').tasks.find().toArray()"

------------------------------------------------------------
🔒 STEP 6 — NOTES ON SECURITY & VALIDATION
------------------------------------------------------------
- Uses a whitelist + blacklist approach for command validation.
- Allowed commands: echo, date, whoami, ls/dir, pwd, cat.
- Destructive commands are blocked.
- In Kaiburr Task 2 (Kubernetes), execution will move to ephemeral BusyBox pods (safer multi-tenant design).

------------------------------------------------------------
✅ STEP 7 — VALIDATION EXAMPLE
------------------------------------------------------------
- Created task with owner Dharalakshmi and command echo Hello Kaiburr!
- Executed the task and verified output "Hello Kaiburr!" with ExitCode: 0.
- Confirmed task persistence via mongosh inside Docker.

------------------------------------------------------------
🧩 TECH STACK
------------------------------------------------------------
- Java (Spring Boot)
- MongoDB (Docker)
- Maven (Build Tool)
- PowerShell / curl (Testing)

------------------------------------------------------------
📚 LICENSE
------------------------------------------------------------
This project is part of the Kaiburr Internship Task for learning and demonstration purposes.
