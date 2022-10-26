package com.github.tomaszgryczka.testowanie;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class APITests {

    static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("roberm", "robert@maklowicz.pl", "Robert", "Maklowicz"),
                Arguments.of("marcink", "marcin@kulima.pl", "Marcin", "Kulima"),
                Arguments.of("hannaj", "hanna@jagielonka.pl", "Hanna", "Jagielonka"),
                Arguments.of("patrycjap", "patrycja@partycja.pl", "Patrycja", "Partycja"),
                Arguments.of("misiuke", "eug@misiuk.pl", "Misio", "Eugenio"),
                Arguments.of("elzbietak", "elzbieta@krol.pl", "Elzbieta", "Krol"),
                Arguments.of("adamz", "adam@zdun.pl", "Adam", "Zdun"),
                Arguments.of("jakubn", "jakub@nowak.pl", "Jakub", "Nowak"),
                Arguments.of("hubertu", "hubert@urban.pl", "Hubert", "Urban"),
                Arguments.of("janj", "janek@jakis.pl", "Jan", "Jakis")
        );
    }

    private ObjectMapper objectMapper;
    private final static String API_URI = "http://localhost:3000/";

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    //GET
    @SneakyThrows
    @ParameterizedTest
    @MethodSource("generateData")
    public void createMethodShouldCreateUserInDatabase(String login, String email, String firstName, String lastName) {
        //given
        User userPost = User.builder()
                .login(login)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        String JSON_STRING = objectMapper.writeValueAsString(userPost);
        StringEntity requestEntity = new StringEntity(
                JSON_STRING,
                ContentType.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(API_URI + "createuser");
        httpPost.setEntity(requestEntity);
        //when
        HttpResponse responsePost = HttpClientBuilder.create().build().execute(httpPost);
        HttpGet httpGet = new HttpGet(API_URI + "user/" + login);
        HttpResponse responseGet = HttpClientBuilder.create().build().execute(httpGet);
        final User userGet = RetrieveUtil.retrieveResourceFromResponse(responseGet, User.class);
        cleanUpUser(userGet);
        //then
        assertEquals(userPost,userGet);
    }

    //POST
    @SneakyThrows
    @ParameterizedTest
    @MethodSource("generateData")
    public void getMethodShouldReturnUser(String login, String email, String firstName, String lastName) {
        //given
        User userPost = User.builder()
                .login(login)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        String JSON_STRING = objectMapper.writeValueAsString(userPost);
        StringEntity requestEntity = new StringEntity(
                JSON_STRING,
                ContentType.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(API_URI + "createuser");
        httpPost.setEntity(requestEntity);
        //when
        HttpResponse responsePost = HttpClientBuilder.create().build().execute(httpPost);
        HttpGet httpGet = new HttpGet(API_URI + "user/" + login);
        HttpResponse responseGet = HttpClientBuilder.create().build().execute(httpGet);
        final User userGet = RetrieveUtil.retrieveResourceFromResponse(responseGet, User.class);
        cleanUpUser(userGet);
        //then
        assertEquals(userPost,userGet);
    }

    //DELETE
    @SneakyThrows
    @ParameterizedTest
    @MethodSource("generateData")
    public void deleteMethodShouldDeleteUserFromDatabase(String login, String email, String firstName, String lastName) {
        //given
        User userPost = User.builder()
                .login(login)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        String JSON_STRING = objectMapper.writeValueAsString(userPost);
        StringEntity requestEntity = new StringEntity(
                JSON_STRING,
                ContentType.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(API_URI + "createuser");
        httpPost.setEntity(requestEntity);
        HttpResponse responsePost = HttpClientBuilder.create().build().execute(httpPost);
        //when
        HttpDelete httpDelete = new HttpDelete(API_URI + "deleteuser/" + login);
        HttpResponse responseDelete = HttpClientBuilder.create().build().execute(httpDelete);
        //then
        HttpGet httpGet = new HttpGet(API_URI + "user/" + login);
        HttpResponse responseGet = HttpClientBuilder.create().build().execute(httpGet);
        assertEquals(404, responseGet.getStatusLine().getStatusCode());

    }
    //PUT
    @SneakyThrows
    @ParameterizedTest
    @MethodSource("generateData")
    public void putMethodShouldUpdateDataInDatabase(String login, String email, String firstName, String lastName) {
        //given
        User userPost = User.builder()
                .login(login)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        String JSON_STRING = objectMapper.writeValueAsString(userPost);
        StringEntity requestEntity = new StringEntity(
                JSON_STRING,
                ContentType.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(API_URI + "createuser");
        httpPost.setEntity(requestEntity);
        HttpResponse responsePost = HttpClientBuilder.create().build().execute(httpPost);
        //when
        User userPut = User.builder()
                .login(login)
                .email("x" + email)
                .firstName(firstName + "x")
                .lastName(lastName + "x")
                .build();
        JSON_STRING = objectMapper.writeValueAsString(userPut);
        requestEntity = new StringEntity(
                JSON_STRING,
                ContentType.APPLICATION_JSON);
        HttpPut httpPut = new HttpPut(API_URI + "user");
        httpPut.setEntity(requestEntity);
        HttpResponse responsePut = HttpClientBuilder.create().build().execute(httpPut);
        //then
        HttpGet httpGet = new HttpGet(API_URI + "user/" + login);
        HttpResponse responseGet = HttpClientBuilder.create().build().execute(httpGet);
        final User userGet = RetrieveUtil.retrieveResourceFromResponse(responseGet, User.class);
        cleanUpUser(userGet);
        assertEquals(userPut,userGet);
    }

    @ParameterizedTest
    @MethodSource("generateData")
    public void putMethodShouldReturn404WhenUserNotExists(String login, String email, String firstName, String lastName) throws IOException {
        // given
        final User notExistingUser = User.builder()
                .login(login)
                .email("x" + email)
                .firstName(firstName + "x")
                .lastName(lastName + "x")
                .build();
        StringEntity requestEntity = new StringEntity(
                objectMapper.writeValueAsString(notExistingUser),
                ContentType.APPLICATION_JSON);
        HttpPut httpPut = new HttpPut(API_URI + "user");
        httpPut.setEntity(requestEntity);

        // when
        HttpResponse responsePut = HttpClientBuilder.create().build().execute(httpPut);

        // then
        assertEquals(404, responsePut.getStatusLine().getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("generateData")
    public void should_Return403HttpCode_When_CreateUserThatAlreadyExists(String login, String email, String firstName, String lastName) throws IOException {
        // given
        final User user = User.builder()
                .login(login)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        StringEntity requestEntity = new StringEntity(
                objectMapper.writeValueAsString(user),
                ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(API_URI + "createuser");
        request.setEntity(requestEntity);

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        HttpResponse forbiddenResponse = HttpClientBuilder.create().build().execute(request);

        cleanUpUser(user);

        // then
        assertEquals(403, forbiddenResponse.getStatusLine().getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("generateData")
    public void should_Return400HttpCode_When_UserNotFound(String login, String email, String firstName, String lastName) throws IOException {
        // given
        final String notExistingUser = "notExistingUser";

        HttpUriRequest request = new HttpGet(API_URI + "user/" + notExistingUser);

        // when
        HttpResponse forbiddenResponse = HttpClientBuilder.create().build().execute(request);

        // then
        assertEquals(404, forbiddenResponse.getStatusLine().getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("generateData")
    public void should_Return403HttpCode_When_PostRequestIsInvalid(String login, String email, String firstName, String lastName) throws IOException {
        // given
        final User user = User.builder()
                .login(login)
                .firstName(firstName)
                .build();

        StringEntity requestEntity = new StringEntity(
                objectMapper.writeValueAsString(user),
                ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(API_URI + "createuser");
        request.setEntity(requestEntity);

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        HttpResponse forbiddenResponse = HttpClientBuilder.create().build().execute(request);

        cleanUpUser(user);

        // then
        assertEquals(400, forbiddenResponse.getStatusLine().getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("generateData")
    public void should_Return403HttpCode_When_PutRequestIsInvalid(String login, String email, String firstName, String lastName) throws IOException {
        // given
        final User user = User.builder()
                .login(login)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        StringEntity requestEntity = new StringEntity(
                objectMapper.writeValueAsString(user),
                ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(API_URI + "createuser");
        request.setEntity(requestEntity);

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        HttpResponse forbiddenResponse = HttpClientBuilder.create().build().execute(request);

        cleanUpUser(user);

        // then
        assertEquals(400, forbiddenResponse.getStatusLine().getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("generateData")
    public void should_Return403HttpCode_When_EmailIsInvalid(String login, String email, String firstName, String lastName) throws IOException {
        // given
        final User user = User.builder()
                .login(login)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        StringEntity requestEntity = new StringEntity(
                objectMapper.writeValueAsString(user),
                ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(API_URI + "createuser");
        request.setEntity(requestEntity);

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        HttpResponse forbiddenResponse = HttpClientBuilder.create().build().execute(request);

        cleanUpUser(user);

        // then
        assertEquals(403, forbiddenResponse.getStatusLine().getStatusCode());
    }


    @SneakyThrows
    @Test
    public void getAllMethodShouldReturnAllUsersInDatabase(){
        //given
        User user1 = User.builder()
                .login("jakubn")
                .email("jakub@nowak.pl")
                .firstName("Jakub")
                .lastName("Nowak")
                .build();
        User user2 = User.builder()
                .login("hubertu")
                .email("hubert@urban.pl")
                .firstName("Hubert")
                .lastName("Urban")
                .build();
        String JSON_STRING = objectMapper.writeValueAsString(user1);
        StringEntity requestEntity = new StringEntity(
                JSON_STRING,
                ContentType.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(API_URI + "createuser");
        httpPost.setEntity(requestEntity);
        HttpResponse response = HttpClientBuilder.create().build().execute(httpPost);
        JSON_STRING = objectMapper.writeValueAsString(user2);
        requestEntity = new StringEntity(
                JSON_STRING,
                ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);
        response = HttpClientBuilder.create().build().execute(httpPost);
        //when
        HttpGet httpGet = new HttpGet(API_URI + "users");
        HttpResponse responseGet = HttpClientBuilder.create().build().execute(httpGet);
        List<User> users = RetrieveUtil.retrieveResourcesFromResponse(responseGet, User.class);
        cleanUpUser(user1);
        cleanUpUser(user2);
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        assertTrue(users.size()==2);
    }

    @SneakyThrows
    private void cleanUpUser(User user) throws IOException {
        HttpDelete httpDelete = new HttpDelete(API_URI + "deleteUser/" + user.getLogin());
        HttpResponse responseDelete = HttpClientBuilder.create().build().execute(httpDelete);
    }

}
