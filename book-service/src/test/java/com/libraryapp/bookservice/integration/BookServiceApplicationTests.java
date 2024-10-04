package com.libraryapp.bookservice.integration;

import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.dto.UpdateBookDtoV1;
import com.libraryapp.bookservice.uti.DataUtils;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("integration")
class BookServiceApplicationTests {

    public static final String API_BOOKS_PATH = "/v1/books";

    @Value("${keycloak.integration-test.admin-jwt}")
    String adminJwt;

    @Autowired
    private TestRestTemplate testRestTemplate;

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
    void create() {
        RequestBookDtoV1 requestBookDtoV1 = DataUtils.createRequestBookDto();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + adminJwt);

        ResponseEntity<ResponseBookDtoV1> postResponse = testRestTemplate.exchange(
                API_BOOKS_PATH,
                POST,
                new HttpEntity<>(requestBookDtoV1, httpHeaders),
                ResponseBookDtoV1.class
        );

        assertEquals(CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertEquals(requestBookDtoV1.getTitle(), postResponse.getBody().getTitle());
    }

    @Test
    void getMany() {
        String ids = "1,2";

        ResponseEntity<ResponseBookDtoV1[]> getResponse = testRestTemplate.getForEntity(
                API_BOOKS_PATH + "/by-ids?ids={ids}",
                ResponseBookDtoV1[].class,
                ids
        );

        assertEquals(OK, getResponse.getStatusCode());
        assertThat(getResponse.getBody()).hasSize(2);
    }

    @Test
    void getOne() {
        Long id = 1L;

        ResponseEntity<ResponseBookDtoV1> getResponse = testRestTemplate.getForEntity(
                API_BOOKS_PATH + "/{id}",
                ResponseBookDtoV1.class,
                id
        );

        assertEquals(OK, getResponse.getStatusCode());
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(getResponse.getBody()).getId()).isEqualTo(id);
    }

    @Test
    void getList() {
        String isbnLike = "";
        String pageNumber = "0";
        String pageSize = "2";
        String sort = "";

        ResponseEntity<?> getResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "?isbnLike={isbnLike}&pageNumber={pageNumber}&pageSize={pageSize}&sort={sort}",
                HttpMethod.GET,
                null,
                Object.class,
                isbnLike,
                pageNumber,
                pageSize,
                sort
        );

        assertEquals(OK, getResponse.getStatusCode());
        assertThat(getResponse.getBody()).isNotNull();
    }

    @Test
    @DirtiesContext
    void patchMany() {
        String ids = "1,2";
        UpdateBookDtoV1 updateBookDtoV1 = DataUtils.createUpdateBookDto();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + adminJwt);

        ResponseEntity<Long[]> patchResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "?ids={ids}",
                HttpMethod.PATCH,
                new HttpEntity<>(updateBookDtoV1, httpHeaders),
                Long[].class,
                ids
        );

        assertEquals(OK, patchResponse.getStatusCode());
    }

    @Test
    @DirtiesContext
    void patch() {
        Integer id = 1;
        UpdateBookDtoV1 updateBookDtoV1 = DataUtils.createUpdateBookDto();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Authorization", "Bearer " + adminJwt);

        ResponseEntity<ResponseBookDtoV1> patchResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(updateBookDtoV1, httpHeaders),
                ResponseBookDtoV1.class,
                id
        );

        assertEquals(OK, patchResponse.getStatusCode());
    }

    @Test
    @DirtiesContext
    void deleteMany() {
        String ids = "1,2";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminJwt);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<?> deleteResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "?ids={ids}",
                HttpMethod.DELETE,
                entity,
                void.class,
                ids
        );

        assertEquals(OK, deleteResponse.getStatusCode());
    }

    @Test
    @DirtiesContext
    void delete() {
        Integer id = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminJwt);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ResponseBookDtoV1> deleteResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/{id}",
                HttpMethod.DELETE,
                entity,
                ResponseBookDtoV1.class,
                id
        );

        assertEquals(OK, deleteResponse.getStatusCode());
    }
}
