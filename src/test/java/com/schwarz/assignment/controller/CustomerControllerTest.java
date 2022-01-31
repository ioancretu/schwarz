package com.schwarz.assignment.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.schwarz.assignment.dto.User;
import com.schwarz.assignment.model.Customer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerControllerTest {
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
    void createCustomer() throws IOException {
        Customer customer = new Customer("Some Name", "some.email@address.com");

        HttpPost request = new HttpPost("http://localhost:8080/api/customers");
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(customer);
        HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(httpEntity);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    void updateCustomer() throws IOException {
//        creating a customer
        Customer customer = new Customer("Some Name1", "some.email@address.com");
        HttpPost request = new HttpPost("http://localhost:8080/api/customers");
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(customer);
        HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(httpEntity);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        customer = objectMapper.readValue(response.getEntity().getContent(), Customer.class);
        customer.setName("a random name");

        HttpPut putRequest = new HttpPut("http://localhost:8080/api/customers/" + customer.getId());
        putRequest.addHeader("Authorization", token);
        json = ow.writeValueAsString(customer);
        httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        putRequest.setEntity(httpEntity);

        response = HttpClientBuilder.create().build().execute(putRequest);


        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    void deleteCustomer() throws IOException {
        Customer customer = new Customer("Some Name1", "some.email@address.com");
        HttpPost request = new HttpPost("http://localhost:8080/api/customers");
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(customer);
        HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(httpEntity);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        customer = objectMapper.readValue(response.getEntity().getContent(), Customer.class);

        HttpDelete deleteRequest = new HttpDelete("http://localhost:8080/api/customers/" + customer.getId());
        deleteRequest.addHeader("Authorization", token);

        response = HttpClientBuilder.create().build().execute(deleteRequest);

        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
    }
}