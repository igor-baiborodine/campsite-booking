# Campsite Booking API  
![Master Branch](https://github.com/igor-baiborodine/campsite-booking/workflows/Build%20Master%20Branch/badge.svg)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=igor-baiborodine_campsite-booking&metric=alert_status)](https://sonarcloud.io/dashboard?id=igor-baiborodine_campsite-booking) 
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=igor-baiborodine_campsite-booking&metric=coverage)](https://sonarcloud.io/summary/new_code?id=igor-baiborodine_campsite-booking)

### A RESTful web service that manages campsite bookings. 

Read these articles to get more insights:
* [Campsite Booking API: Revisited](https://www.kiroule.com/article/campsite-booking-api-revisited/)
* [Campsite Booking API: Revisited 2](https://www.kiroule.com/article/campsite-booking-api-revisited-2/)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Technical Task](#technical-task)
  - [Booking Constraints](#booking-constraints)
  - [System Requirements](#system-requirements)
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
  - [Build on Pull Request](#build-on-pull-request)
  - [Build Master Branch](#build-master-branch)
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

## Up & Running
### Run with Maven
```bash
$ git clone https://github.com/igor-baiborodine/campsite-booking.git
$ cd campsite-booking
$ mvn spring-boot:run -Dspring-boot.run.profiles=in-memory-db
```
The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Executable JAR
```bash
$ git clone https://github.com/igor-baiborodine/campsite-booking.git
$ cd campsite-booking
$ mvn package -DskipTests
$ java -jar -Dspring.profiles.active=in-memory-db target/campsite-booking-<version>.jar
```
The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Docker
```bash
$ git clone https://github.com/igor-baiborodine/campsite-booking.git
$ cd campsite-booking
$ docker build --rm -t campsite-booking .
$ docker run -e "SPRING_PROFILES_ACTIVE=in-memory-db" --name campsite-booking -d campsite-booking
$ docker logs -f campsite-booking 
```
The Swagger UI is available at `http://container-ip:8080/swagger-ui.html`. To get the container IP address, execute the following command:
```console
$ docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' campsite-booking
```
Via the host machine on port 80:
```console
$ docker run -e "SPRING_PROFILES_ACTIVE=in-memory-db" --name campsite-booking -p 80:8080 -d campsite-booking
```
The Swagger UI is available at `http://localhost:80/swagger-ui.html` or `http://host-ip:80/swagger-ui.html`.

... or with an [image from Docker Hub](https://hub.docker.com/r/ibaiborodine/campsite-booking):
```console
$ docker run -e "SPRING_PROFILES_ACTIVE=in-memory-db" --name campsite-booking -p 80:8080 -d ibaiborodine/campsite-booking
```
... or with in-memory DB [docker-compose](../docker-compose.yml):
```console
$ docker-compose up -d
```
... or with MySQL [docker-compose](../mysql/docker-compose.yml):
```console
$ mysql/docker-compose up -d
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
$ mvn clean verify sonar:sonar -Dsonar.login=<SONAR_TOKEN> -Pcoverage
```

### Swagger UI
The API can be tested via the Swagger UI:

![Swagger UI Main View](/readme/swagger-main-view.png)

For example, to add a new booking, expand the `POST` operation. Then click on the `Try it out`, add the payload below to
the `Request Body` text area, and click on the `Execute`:

```json
{
  "campsiteId": 1,
  "email": "John Smith",
  "fullName": "john.smith@email.com",
  "startDate": "2023-10-29",
  "endDate": "2023-10-30",
  "active": true
}
```
![Swagger UI Add Booking 1](/readme/swagger-add-booking-1.png)

If the operation is successful, you will get the following response:

![Swagger UI Add Booking 1](/readme/swagger-add-booking-2.png)

### Concurrent Tests

Start an instance of the Campsite Booking API via Docker Compose either in [the in-memory-db](../docker-compose.yml) or
in [mysql](../mysql/docker-compose.yml) profile.
```bash
$ docker-compose.yml up -d
```

#### Bookings Creation
Execute the [create-bookings.sh](../script/test/create-bookings.sh) script to simulate concurrent
booking creation for the same booking dates:

```bash
$ ./script/test/create-bookings.sh 2023-10-16 2023-10-17 http:/localhost:80
```
The response should be as follows after formatting, i.e., only one booking was created:
```json
[
  {
    "status": "BAD_REQUEST",
    "timestamp": "2023-10-15T16:36:53.563620238",
    "message": "No vacant dates available from 2023-10-16 to 2023-10-17"
  },
  {
    "status": "BAD_REQUEST",
    "timestamp": "2023-10-15T16:36:53.568417093",
    "message": "No vacant dates available from 2023-10-16 to 2023-10-17"
  },
  {
    "uuid": "870cdaaa-592e-4f9e-92e4-8168f7d427b7",
    "version": 0,
    "campsiteId": 1,
    "email": "john.smith.3@email.com",
    "fullName": "John Smith 3",
    "startDate": "2023-10-16",
    "endDate": "2023-10-17",
    "active": true,
    "_links": {
      "self": {
        "href": "http://localhost:8080/v2/booking/870cdaaa-592e-4f9e-92e4-8168f7d427b7"
      }
    }
  }
]
```

#### Booking Update
Execute the [update-booking.sh](../script/test/update-booking.sh) script to simulate concurrent
updates for the same booking:

```bash
$ ./script/test/update-booking.sh 2022-10-25 2022-10-26 http:/localhost:80
```
The response should be as follows after formatting, i.e., only one booking was updated:
```json
[
  {
    "uuid": "cbf91bf1-c533-4c3d-a9c5-c1e3b0396306",
    "version": 0,
    "campsiteId": 3,
    "email": "john.smith.1@email.com",
    "fullName": "John Smith 1",
    "startDate": "2023-10-25",
    "endDate": "2023-10-26",
    "active": true,
    "_links": {
      "self": {
        "href": "http://localhost:8080/v2/booking/cbf91bf1-c533-4c3d-a9c5-c1e3b0396306"
      }
    }
  },
  {
    "status": "CONFLICT",
    "timestamp": "2023-10-15T17:04:18.606047801",
    "message": "Optimistic locking error - booking was updated by another transaction"
  }
]
```

### Basic Load Testing 
Basic load testing for retrieving vacant dates can be performed with the ApacheBench by executing the following command:
```Bash
$ docker-compose.yml up -d
$ ab -n 10000 -c 100 -k http://localhost:80/v2/booking/vacant-dates
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

Document Path:          /v2/booking/vacant-dates
Document Length:        365 bytes

Concurrency Level:      100
Time taken for tests:   4.250 seconds
Complete requests:      10000
Failed requests:        0
Keep-Alive requests:    0
Total transferred:      4700000 bytes
HTML transferred:       3650000 bytes
Requests per second:    2352.71 [#/sec] (mean)
Time per request:       42.504 [ms] (mean)
Time per request:       0.425 [ms] (mean, across all concurrent requests)
Transfer rate:          1079.86 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.6      0       9
Processing:     3   42  33.4     32     339
Waiting:        2   41  33.0     32     338
Total:          3   42  33.5     32     339

Percentage of the requests served within a certain time (ms)
  50%     32
  66%     50
  75%     56
  80%     59
  90%     85
  95%    107
  98%    144
  99%    156
 100%    339 (longest request)
```

## Continuous Integration

Continuous integration is implemented using GitHub Actions, and it includes the `Build on Pull Request`
, `Build Master Branch`, and `Perform Release` workflows:

![GitHub Actions](/readme/github-actions.png)

### Build on Pull Request

This workflow is executed automatically on any pull request and consists of the `Unit & Integrations Tests`
and `SonarCloud Scan` job:

![Build on Pull Request Workflow](/readme/github-actions-build-on-pull-request.png)

### Build Master Branch

This workflow is executed automatically on any commit to the `master` branch and consists of the `SonarCloud Scan`
and `Snapshot Publishing` jobs:

![Build Master Branch Workflow](/readme/github-actions-build-master-branch.png)

### Perform Release
This workflow is executed manually and consists of the `Maven Release` and `Docker Image` jobs:

![Perform Release Workflow](/readme/github-actions-perform-release.png)

The `Release Version` parameter value should be provided before executing this workflow:

![Perform Release Workflow](/readme/github-actions-perform-release-parameter.png)
