package com.kiroule.campsitebooking;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("mysql")
public abstract class BaseIT {

  private static final String MYSQL_DOCKER_IMAGE_NAME = "mysql:8-debian";
  private static final String MYSQL_DATABASE_NAME = "test_campsite";

  static final MySQLContainer<?> mySqlContainer;

  static {
    mySqlContainer =
        new MySQLContainer<>(MYSQL_DOCKER_IMAGE_NAME).withDatabaseName(MYSQL_DATABASE_NAME);
    mySqlContainer.start();
  }

  @DynamicPropertySource
  static void mysqlProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
    registry.add("spring.datasource.username", mySqlContainer::getUsername);
    registry.add("spring.datasource.password", mySqlContainer::getPassword);
    registry.add("spring.jpa.properties.hibernate.show_sql", () -> "true");
  }
}
