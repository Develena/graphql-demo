package com.example.graphqldemo.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class) // JUnit5
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeResolverTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    void employeeInfoTest() throws IOException {

        GraphQLResponse response = graphQLTestTemplate.postForResource("employee.graphqls");

        JsonNode result = response.readTree().get("data");
        Assertions.assertNotNull(result.asText());

    }

}
