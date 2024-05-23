# Campsite Booking API (Java)  
![Master Branch](https://github.com/igor-baiborodine/campsite-booking/workflows/Build%20Master%20Branch/badge.svg)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=igor-baiborodine_campsite-booking&metric=alert_status)](https://sonarcloud.io/dashboard?id=igor-baiborodine_campsite-booking) 
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=igor-baiborodine_campsite-booking&metric=coverage)](https://sonarcloud.io/summary/new_code?id=igor-baiborodine_campsite-booking)

🔥 A Go version is available in the [campsite-booking-go](https://github.com/igor-baiborodine/campsite-booking-go) repository. 

### A RESTful web service that manages campsite bookings. 

Read these articles to get more insights:
* [Campsite Booking API: Revisited](https://www.kiroule.com/article/campsite-booking-api-revisited/)
* [Campsite Booking API: Revisited 2](https://www.kiroule.com/article/campsite-booking-api-revisited-2/)
* [Campsite Booking API: Revisited 3](https://www.kiroule.com/article/campsite-booking-api-revisited-3/)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Technical Task](#technical-task)
  - [Booking Constraints](#booking-constraints)
  - [System Requirements](#system-requirements)
- [Implementation Details](#implementation-details)
- [Up & Running](#up--running)
  - [Run with Maven](#run-with-maven)
  - [Executable JAR](#executable-jar)
  - [Docker](#docker)
- [Tests](#tests)
  - [Maven](#maven)
  - [Swagger UI](#swagger-ui)
  - [Concurrent Tests](#concurrent-tests)
    - [Bookings Creation](#bookings-creation)
    - [Booking Update](#booking-update)
  - [Basic Load Testing](#basic-load-testing)
- [Continuous Integration](#continuous-integration)
  - [Build Master Branch](#build-master-branch)
  - [Build on Pull Request](#build-on-pull-request)
  - [Generate README TOC](#generate-readme-toc)
  - [Perform Release](#perform-release)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Technical Task

### Booking Constraints
* The campsite can be reserved for max 3 days.
* The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
* Reservations can be cancelled anytime.
* For sake of simplicity assume the check-in & check-out time is 12:00 AM.

### System Requirements
* The users will need to find out when the campsite is available. So the system should expose an API to provide information of the
availability of the campsite for a given date range with the default being 1 month.
* Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite
along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful.
* The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow
modification/cancellation of an existing reservation.
* Due to the popularity of the campsite, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping
date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.
* Provide appropriate error messages to the caller to indicate the error cases.
* The system should be able to handle large volume of requests for getting the campsite availability.
* There are no restrictions on how reservations are stored as long as system constraints are not violated.

## Implementation Details

This project is implemented using an API-first(or contract-first) approach along with Maven's
multi-module project structure. You can read
more [here](https://swagger.io/resources/articles/adopting-an-api-first-approach/) on the API-first
development approach.

## Up & Running
### Run with Maven
```bash
$ git clone https://github.com/igor-baiborodine/campsite-booking.git
$ cd campsite-booking
$ mvn clean install -DskipTests -DskipITs
$ mvn spring-boot:run -Dspring-boot.run.profiles=in-memory-db -f campsite-booking-service/pom.xml
```
The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Executable JAR
```bash
$ git clone https://github.com/igor-baiborodine/campsite-booking.git
$ cd campsite-booking
$ mvn clean install -DskipTests -DskipITs
$ mvn package spring-boot:repackage -DskipTests -DskipITs -f campsite-booking-service/pom.xml
$ java -jar -Dspring.profiles.active=in-memory-db campsite-booking-service/target/campsite-booking-service-<version>.jar
```
The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Docker
```bash
$ git clone https://github.com/igor-baiborodine/campsite-booking.git
$ cd campsite-booking
$ docker build --rm --file container/Dockerfile --tag campsite-booking-service .
$ docker run -e "SPRING_PROFILES_ACTIVE=in-memory-db" --name campsite-booking-service -d campsite-booking-service
$ docker logs -f campsite-booking-service 
```

The Swagger UI is available at `http://<container-ip>:8080/swagger-ui.html`. To get the container IP
address, execute the following command:

```console
$ docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' campsite-booking-service
```

Via the host machine on port 80:
```console
$ docker run -e "SPRING_PROFILES_ACTIVE=in-memory-db" --name campsite-booking-service -p 80:8080 -d campsite-booking-service
```
The Swagger UI is available at `http://localhost:80/swagger-ui.html` or `http://host-ip:80/swagger-ui.html`.

... or with an [image from Docker Hub](https://hub.docker.com/r/ibaiborodine/campsite-booking):
```console
$ docker run -e "SPRING_PROFILES_ACTIVE=in-memory-db" --name campsite-booking-service -p 80:8080 -d ibaiborodine/campsite-booking-service
```
... or with in-memory DB [docker-compose](../container/docker-compose.yml):
```console
$ docker compose -f container/docker-compose.yml up -d
```
... or with MySQL [docker-compose](../container/campsite-booking-service-mysql/docker-compose.yml):
```console
$ docker compose -f container/campsite-booking-service-mysql/docker-compose.yml up -d
```
 
## Tests

### Maven
* Run only unit tests:
```bash
$ mvn clean test
```
* Run only integration tests:
```bash
$ mvn clean failsafe:integration-test
```
* Run unit and integration tests:
```bash
$ mvn clean verify
```
* Run SonarCloud analysis, including test coverage, code smells, vulnerabilities, etc.:
```bash
$ mvn clean verify sonar:sonar -Dsonar.login=<SONAR_TOKEN>
```

### Swagger UI
The API can be tested via the Swagger UI:

![Swagger UI Main View](/readme/swagger-main-view.png)

For example, to add a new booking, expand the `POST` operation. Then click on the `Try it out`, add
the payload below to the `Request Body` text area, and click on the `Execute`:

```json
{
  "campsiteId": 1,
  "email": "John Smith",
  "fullName": "john.smith@email.com",
  "startDate": "2023-11-19",
  "endDate": "2023-11-21",
  "active": true
}
```
![Swagger UI Add Booking 1](/readme/swagger-add-booking-1.png)

If the operation is successful, you will get the following response:

![Swagger UI Add Booking 1](/readme/swagger-add-booking-2.png)

### Concurrent Tests

Start an instance of the Campsite Booking API via Docker Compose either
in [the in-memory-db](../container/docker-compose.yml) or
in [mysql](../container/campsite-booking-service-mysql/docker-compose.yml) profile.

```bash
$ docker-compose.yml up -d
```

#### Bookings Creation

Execute the [concurrent-create-bookings.sh](../script/test/concurrent-create-bookings.sh) script to
simulate concurrent booking creation for the same booking dates:

```bash
$ ./script/test/concurrent-create-bookings.sh 2023-11-16 2023-11-17 http:/localhost:80
```
The response should be as follows after formatting, i.e., only one booking was created:
```json
[
  {
    "uuid": "4da1818c-2d9e-4efe-b59d-38915d6bc5d3",
    "version": 0,
    "campsiteId": 1,
    "email": "john.smith.2@email.com",
    "fullName": "John Smith 2",
    "startDate": "2023-11-16",
    "endDate": "2023-11-17",
    "active": true
  },
  {
    "status": 400,
    "message": "No vacant dates available from 2023-11-16 to 2023-11-17",
    "timestamp": "2023-11-12T20:31:23.751+00:00",
    "subErrors": []
  },
  {
    "status": 400,
    "message": "No vacant dates available from 2023-11-16 to 2023-11-17",
    "timestamp": "2023-11-12T20:31:23.756+00:00",
    "subErrors": []
  }
]
```

#### Booking Update

Execute the [concurrent-update-booking.sh](../script/test/concurrent-update-booking.sh) script to
simulate concurrent updates for the same booking:

```bash
$ ./script/test/concurrent-update-booking.sh 2023-11-15 2023-11-16 http:/localhost:80
```
The response should be as follows after formatting, i.e., only one booking was updated:
```json
[
  {
    "uuid": "ea10008b-c60e-41f9-97bb-313e9502e7f4",
    "version": 1,
    "campsiteId": 3,
    "email": "john.smith.1@email.com",
    "fullName": "John Smith 1",
    "startDate": "2023-11-15",
    "endDate": "2023-11-16",
    "active": true
  },
  {
    "status": 409,
    "message": "Optimistic locking error: com.kiroule.campsitebooking.repository.entity.BookingEntity with id 1 was updated by another transaction",
    "timestamp": "2023-11-12T20:29:55.008+00:00",
    "subErrors": []
  }
]
```

### Basic Load Testing 
Basic load testing for retrieving vacant dates can be performed with the ApacheBench by executing the following command:
```Bash
$ docker-compose.yml up -d
$ ab -n 10000 -c 100 -k http://localhost:80/api/v2/booking/vacant-dates
```
* **-n 10000** is the number of requests to make
* **-c 100** is the number of concurrent requests to make at a time
* **-k** sends the **KeepAlive** header, which asks the web server to not shut down the connection after each request is done, but to instead keep reusing it

Result:
```
Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests

Server Software:        
Server Hostname:        localhost
Server Port:            80

Document Path:          /api/v2/booking/vacant-dates
Document Length:        159 bytes

Concurrency Level:      100
Time taken for tests:   2.134 seconds
Complete requests:      10000
Failed requests:        0
Non-2xx responses:      10000
Keep-Alive requests:    0
Total transferred:      2720000 bytes
HTML transferred:       1590000 bytes
Requests per second:    4685.95 [#/sec] (mean)
Time per request:       21.340 [ms] (mean)
Time per request:       0.213 [ms] (mean, across all concurrent requests)
Transfer rate:          1244.70 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   0.8      1       7
Processing:     2   20   9.6     19      88
Waiting:        1   19   9.0     18      87
Total:          2   21   9.3     19      88

Percentage of the requests served within a certain time (ms)
  50%     19
  66%     23
  75%     25
  80%     27
  90%     32
  95%     38
  98%     47
  99%     55
 100%     88 (longest request)
```

## Continuous Integration

Continuous integration is implemented using GitHub Actions, and it includes
the `Build Master Branch`, `Build on Pull Request`, `Generate README TOC`, and `Perform Release`
workflows:

![GitHub Actions](/readme/github-actions.png)

### Build Master Branch

This workflow is executed automatically on any commit to the `master` branch and consists of
the `SonarCloud Scan` and `Snapshot Publishing` jobs:

![Build Master Branch Workflow](/readme/github-actions-build-master-branch.png)

### Build on Pull Request

This workflow is executed automatically on any pull request and consists of
the `Unit & SonarCloud Scan` job:

![Build on Pull Request Workflow](/readme/github-actions-build-on-pull-request.png)

### Generate README TOC

This workflow is executed automatically on any update of the `readme/README.md` file pushed to
the `master` branch and consists of the `Generate TOC` job:

![Generate README TOC Workflow](/readme/github-actions-generate-readme-toc.png)

### Perform Release
This workflow is executed manually and consists of the `Maven Release` and `Docker Image` jobs:

![Perform Release Workflow](/readme/github-actions-perform-release.png)

The `Release Version` parameter value should be provided before executing this  workflow:

![Perform Release Workflow](/readme/github-actions-perform-release-parameter.png)
