### Developed with pleasure!
![IntelliJ IDEA Logo](https://www.kiroule.com/img/content/page/projects/intellij-idea-logo.png)
 
# Campsite Booking API  
![Master Branch](https://github.com/igor-baiborodine/campsite-booking/workflows/Master%20Branch/badge.svg)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=igor-baiborodine_campsite-booking&metric=alert_status)](https://sonarcloud.io/dashboard?id=igor-baiborodine_campsite-booking) 
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=igor-baiborodine_campsite-booking&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=igor-baiborodine_campsite-booking)

### A RESTful web service that manages campsite bookings. 

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
### Maven
```bash
$ git clone https://github.com/igor-baiborodine/campsite-booking.git
$ cd campsite-booking
$ mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Executable JAR
```bash
git clone https://github.com/igor-baiborodine/campsite-booking.git
cd campsite-booking
mvn package -DskipTests
java -jar -Dspring.profiles.active=h2 target/campsite-booking-<version>.jar
```
The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Docker
```bash
git clone https://github.com/igor-baiborodine/campsite-booking.git
cd campsite-booking
docker build --rm -t campsite-booking .
docker run -e "SPRING_PROFILES_ACTIVE=h2" --name campsite-booking -d campsite-booking
docker logs -f campsite-booking 
```
The Swagger UI is available at `http://container-ip:8080/swagger-ui.html`. To get the container IP address, execute the following command:
```console
$ docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' campsite-booking
```
Via the host machine on port 80:
```console
$ docker run -e "SPRING_PROFILES_ACTIVE=h2" --name campsite-booking -p 80:8080 -d campsite-booking
```
The Swagger UI is available at `http://localhost:80/swagger-ui.html` or `http://host-ip:80/swagger-ui.html`.

... or with an [image from Docker Hub](https://hub.docker.com/r/ibaiborodine/campsite-booking):
```console
$ docker run -e "SPRING_PROFILES_ACTIVE=h2" --name campsite-booking -p 80:8080 -d ibaiborodine/campsite-booking
```
... or with [docker-compose](docker-compose.yml):
```console
$ docker-compose up -d
```

## Continuous Integration
Continuous integration is implemented using GitHub Actions, and it includes the `Build on Pull Request`, `Build Master Branch`, and `Perform Release` workflows:

![GitHub Actions](/readme/github-actions.png)

### Build on Pull Request
This workflow is executed automatically on any pull request and consists of the `SonarCloud Scan` job:

![Build on Pull Request Workflow](/readme/github-actions-build-on-pull-request.png) 

### Build Master Branch
This workflow is executed automatically on any commit to the `master` branch and consists of the `Unit & Integrations Tests`, `SonarCloud Scan`, and `Snapshot Publishing` jobs:

![Build Master Branch Workflow](/readme/github-actions-build-master-branch.png)

### Perform Release
This workflow is executed manually and consists of the `Maven Release` and `Docker Image` jobs:

![Perform Release Workflow](/readme/github-actions-perform-release.png)

The `Release Version` parameter value should be provided before executing this workflow:

![Perform Release Workflow](/readme/github-actions-perform-release-parameter.png)
 
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
$ mvn clean verfify sonar:sonar -Dsonar.login=<SONAR_TOKEN> -Pcoverage
```

### Swagger UI
The API can be tested via the Swagger UI:

![Swagger UI Main View](/readme/swagger-main-view.png)

For example, to add a new booking, expand the `POST` operation. Then click on the `Try it out`, add the payload below to the `Request Body` text area, and click on the `Execute`:
```json
{
  "uuid": "8db6b1f4-27ba-11eb-adc1-0242ac120001",  
  "email": "john.smith.1@email.com",
  "fullName": "John Smith 1",
  "startDate": "2021-01-21",
  "endDate": "2021-01-23"
}
```
![Swagger UI Add Booking 1](/readme/swagger-add-booking-1.png)

If the operation is successful, you will get the following response:

![Swagger UI Add Booking 1](/readme/swagger-add-booking-2.png)

### H2 Console
When running with the `h2` profile, the H2 console is available at `http://localhost:8080/h2-console`.

Fill the login form as follows and click on Connect:
* Saved Settings: **Generic H2 (Embedded)**
* Setting Name: **Generic H2 (Embedded)**
* Driver class: **org.h2.Driver**
* JDBC URL: **jdbc:h2:mem:campsite;MODE=MySQL**
* User Name: **sa**
* Password:

![H2 Console Login](/readme/h2-console-login.bmp)
![H2 Console Main View](/readme/h2-console-main-view.bmp)

### Concurrent Bookings Creation
To simulate concurrent bookings creation for the same booking dates, create three JSON files with booking data as follows:
```bash
$ {
  echo '{'
  echo '  "uuid": "8db6b1f4-27ba-11eb-adc1-0242ac120001",'  
  echo '  "email": "john.smith.1@email.com",'
  echo '  "fullName": "John Smith 1",'
  echo '  "startDate": "2020-12-11",'
  echo '  "endDate": "2020-12-13"'
  echo '}'
} > booking-john-smith-1.json
```
```Bash
$ {
  echo '{'
  echo '  "uuid": "acc07a12-27ba-11eb-adc1-0242ac120002",'
  echo '  "email": "john.smith.2@email.com",'
  echo '  "fullName": "John Smith 2",'
  echo '  "startDate": "2020-12-11",'
  echo '  "endDate": "2020-12-13"'
  echo '}'
} > booking-john-smith-2.json
```
```Bash
$ {
  echo '{'
  echo '  "uuid": "c7fe1e6a-27ba-11eb-adc1-0242ac120003",'
  echo '  "email": "john.smith.3@email.com",'
  echo '  "fullName": "John Smith 3",'
  echo '  "startDate": "2020-12-11",'
  echo '  "endDate": "2020-12-13",'
  echo '}'
} > booking-john-smith-3.json
```
Then execute the following command to send three concurrent HTTP POST requests:
```Bash
$ curl -H "Content-Type: application/json" -d @booking-john-smith-1.json http://localhost:8080/campsite/api/bookings & \
  curl -H "Content-Type: application/json" -d @booking-john-smith-2.json http://localhost:8080/campsite/api/bookings & \
  curl -H "Content-Type: application/json" -d @booking-john-smith-3.json http://localhost:8080/campsite/api/bookings &
```
The response should be as follows after formatting, i.e., only one booking was created:
```json
{  
   "id":2,
   "version":0,
   "uuid":"8db6b1f4-27ba-11eb-adc1-0242ac120002",
   "email":"john.smith.1@email.com",
   "fullName":"John Smith 1",
   "startDate":"2020-12-11",
   "endDate":"2020-12-13",
   "active":true,
   "_links":{  
      "self":{  
         "href":"http://localhost:8080/campsite/api/bookings/2"
      }
   }
}
{  
   "status":"BAD_REQUEST",
   "timestamp":"2020-11-15T23:02:57.592462",
   "message":"No vacant dates available from 2020-12-11 to 2020-12-13"
}
{  
   "status":"BAD_REQUEST",
   "timestamp":"2020-11-15T23:02:57.618991",
   "message":"No vacant dates available from 2020-12-11 to 2020-12-13"
}
```

### Basic Load Testing 
Basic load testing for retrieving vacant dates can be performed with the ApacheBench by executing the following command:
```Bash
$ ab -n 10000 -c 100 -k http://localhost:8080/campsite/api/bookings/vacant-dates
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
Server Port:            8080

Document Path:          /campsite/api/bookings/vacant-dates
Document Length:        404 bytes

Concurrency Level:      100
Time taken for tests:   5.760 seconds
Complete requests:      10000
Failed requests:        0
Keep-Alive requests:    0
Total transferred:      5230000 bytes
HTML transferred:       4040000 bytes
Requests per second:    1736.24 [#/sec] (mean)
Time per request:       57.596 [ms] (mean)
Time per request:       0.576 [ms] (mean, across all concurrent requests)
Transfer rate:          886.77 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       3
Processing:     4   57  42.2     47     373
Waiting:        3   53  40.5     43     368
Total:          4   57  42.2     47     373

Percentage of the requests served within a certain time (ms)
  50%     47
  66%     62
  75%     76
  80%     85
  90%    113
  95%    142
  98%    180
  99%    201
 100%    373 (longest request)
```

