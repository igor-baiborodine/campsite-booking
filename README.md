# campsite-booking

[![Build
Status](https://travis-ci.org/igor-baiborodine/campsite-booking.svg?branch=master)](https://travis-ci.org/igor-baiborodine/campsite-booking)

A REST service that manages campsite bookings. 

### Technical Task
#### Booking Constraints
* The campsite can be reserved for max 3 days.
* The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
* Reservations can be cancelled anytime.
* For sake of simplicity assume the check-in & check-out time is 12:00 AM.
#### System Requirements
* The users will need to find out when the campsite is available. So the system should expose an API to provide information of the
availability of the campsite for a given date range with the default being 1 month.
* Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite
along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful.
* The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow
modification/cancellation of an existing reservation
* Due to the popularity of the campsite, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping
date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.
* Provide appropriate error messages to the caller to indicate the error cases.
* The system should be able to handle large volume of requests for getting the campsite availability.
* There are no restrictions on how reservations are stored as as long as system constraints are not violated.

### Running Project
* Default active profile: **h2**
* URL to access Campsite Booking service: **http://localhost:8090/campsite/api/bookings/**
#### With Maven
```bash
git clone https://github.com/igor-baiborodine/campsite-booking.git
cd campsite-booking
mvn spring-boot:run
```
#### With Executable JAR
```bash
git clone https://github.com/igor-baiborodine/campsite-booking.git
cd campsite-booking
mvn package -DskipTests
java -jar target/campsite-booking-0.0.1-SNAPSHOT.jar
```

### Accessing Data in H2 Database
#### H2 Console
URL to access H2 console: **http://localhost:8090/campsite/h2-console**

Fill the login form as follows and click on Connect:
* Saved Settings: **Generic H2 (Embedded)**
* Setting Name: **Generic H2 (Embedded)**
* Driver class: **org.h2.Driver**
* JDBC URL: **jdbc:h2:mem:campsite;MODE=MySQL**
* User Name: **sa**
* Password:

![H2 Console Login](/images/h2-console-login.bmp)
![H2 Console Main View](/images/h2-console-main-view.bmp)

### Exploring API
#### Swagger UI
URL to access Swagger UI: **http://localhost:8090/campsite/swagger-ui.html**

### Testing API
#### With Maven
* Run only unit tests:
```bash
mvn clean test
```
* Run unit and integration tests:
```bash
mvn clean integration-test
```
* Run only integration tests:
```bash
mvn clean failsafe:integration-test
```
* Run any checks on results of integration tests to ensure quality criteria are met:
```bash
mvn clean verify
```
#### Unit & Integration Tests Coverage
Results of running tests with coverage in IntelliJ IDEA:
![Test Coverage Results](/images/test-coverage-results.bmp)

#### Concurrent Bookings Creation Test
#### Basic Load Testing


