package com.kiroule.campsite.booking.api;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("in-memory-db")
@DisplayNameGeneration(CustomReplaceUnderscores.class)
public abstract class BaseTestIT {}
