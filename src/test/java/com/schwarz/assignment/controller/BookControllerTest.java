package com.schwarz.assignment.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.schwarz.assignment.dto.User;
import com.schwarz.assignment.model.Book;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookControllerTest {
    private static String token;

    @BeforeAll
    static void beforeAll() throws IOException {
        HttpPost request = new HttpPost("http://localhost:8080/user");
        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("user","test_user"));
        postParameters.add(new BasicNameValuePair("password","randomstuff"));
        request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = objectMapper.readValue(response.getEntity().getContent(), User.class);
        token = user.getToken();
    }

    @Test
    void getAllBooks() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/api/books");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    void getBookById() throws IOException {

        HttpUriRequest request = new HttpGet("http://localhost:8080/api/books/" + 3);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    void createBook() throws IOException {
        Book book = new Book("Frank Herbert", "Dune", "science fiction");

        HttpUriRequest request = new HttpPost("http://localhost:8080/api/books");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    void updateBook() throws IOException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        Book book = new Book("Isaac Asimov", "Foundation", "science fiction");
        HttpPost httpRequest = new HttpPost("http://localhost:8080/api/books");
        httpRequest.addHeader("Authorization", token);
        String json = ow.writeValueAsString(book);
        HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpRequest.setEntity(httpEntity);

        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(httpRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        book = objectMapper.readValue(httpResponse.getEntity().getContent(), Book.class);
        book.setCategory("si-fi");

        HttpPut request = new HttpPut("http://localhost:8080/api/books/" + book.getId());
        request.addHeader("Authorization", token);
        json = ow.writeValueAsString(book);
        httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(httpEntity);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    void deleteBook() throws IOException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        Book book = new Book("Isaac Asimov", "Foundation", "science fiction");
        HttpPost httpRequest = new HttpPost("http://localhost:8080/api/books");
        httpRequest.addHeader("Authorization", token);
        String json = ow.writeValueAsString(book);
        HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpRequest.setEntity(httpEntity);

        HttpResponse response = HttpClientBuilder.create().build().execute(httpRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        book = objectMapper.readValue(response.getEntity().getContent(), Book.class);

        HttpDelete deleteRequest = new HttpDelete("http://localhost:8080/api/books/" + book.getId());
        deleteRequest.addHeader("Authorization", token);
        response = HttpClientBuilder.create().build().execute(deleteRequest);

        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
    }
}