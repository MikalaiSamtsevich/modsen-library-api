package com.libraryapp.libraryservice.integration;

import com.libraryapp.libraryservice.model.dto.ResponseBookStatusDtoV1;
import com.libraryapp.libraryservice.model.dto.UpdateBookStatusDtoV1;
import com.libraryapp.libraryservice.util.DataUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("integration")
class BookStatusApplicationTest {


    public static final String API_BOOKS_PATH = "/v1/books/status";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${keycloak.integration-test.admin-jwt}")
    String adminJwt;

    @Value("${keycloak.integration-test.user-jwt}")
    String userJwt;

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"));


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }


    @Test
    void getFreeBooks() {
        String pageNumber = "0";
        String pageSize = "3";
        String sort = "";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + userJwt);

        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<?> getResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/free?pageNumber={pageNumber}&pageSize={pageSize}&sort={sort}",
                HttpMethod.GET,
                entity,
                Object.class,
                pageNumber,
                pageSize,
                sort
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertThat(getResponse.getBody()).isNotNull();
    }


    @Test
    void getList() {
        String pageNumber = "0";
        String pageSize = "3";
        String sort = "";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + userJwt);

        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<?> getResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "?pageNumber={pageNumber}&pageSize={pageSize}&sort={sort}",
                HttpMethod.GET,
                entity,
                Object.class,
                pageNumber,
                pageSize,
                sort
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertThat(getResponse.getBody()).isNotNull();
    }

    @Test
    void getOne() {
        Long id = 1L;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + userJwt);

        ResponseEntity<ResponseBookStatusDtoV1> getResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                ResponseBookStatusDtoV1.class,
                id
        );
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(getResponse.getBody()).getId()).isEqualTo(id);
    }

    @Test
    void getMany() {
        String ids = "1, 2";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + userJwt);

        ResponseEntity<ResponseBookStatusDtoV1[]> getResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/by-ids?ids={ids}",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                ResponseBookStatusDtoV1[].class,
                ids
        );
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    }

    @Test
    @DirtiesContext
    void patch() {
        String id = "1";
        UpdateBookStatusDtoV1 updateBookStatusDtoV1 = DataUtils.createUpdateBookStatusDto();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + adminJwt);
        ResponseEntity<ResponseBookStatusDtoV1> patchResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(updateBookStatusDtoV1, httpHeaders),
                ResponseBookStatusDtoV1.class,
                id
        );
        assertEquals(HttpStatus.OK, patchResponse.getStatusCode());
    }

    @Test
    @DirtiesContext
    void patchMany() {
        String ids = "1, 2";
        UpdateBookStatusDtoV1 updateBookStatusDtoV1 = DataUtils.createUpdateBookStatusDto();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + adminJwt);

        ResponseEntity<Long[]> patchResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "?ids={ids}",
                HttpMethod.PATCH,
                new HttpEntity<>(updateBookStatusDtoV1, httpHeaders),
                Long[].class,
                ids
        );
        assertEquals(HttpStatus.OK, patchResponse.getStatusCode());
    }
}
