package com.example.graphqldemo.graphql;

import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.com.google.common.base.Charsets;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLProvider {

    private GraphQL graphQL;

    private GraphQLDataFetchers graphQLDataFetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    GraphQLProvider(GraphQLDataFetchers graphQLDataFetchers){
        this.graphQLDataFetchers = graphQLDataFetchers;
    }

    @PostConstruct
    public void init() throws IOException {
        // Guava Resource로부터 정의서(SDL) 가져오기
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
//        String sdl = StreamUtils.copyToString(schemaResource.getInputStream(), StandardCharsets.UTF_8); // Guava 사용하지 않을 경우

        // SDL 로부터 GraphQLSchema, GraphQL 인스턴스 생성
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    /**
     * SDL로부터 스키마 생성
     * @param sdl
     * @return GraphQLSchema
     */
    private GraphQLSchema buildSchema(String sdl) {
        // TypeDefinitionRegistry: 스키마 파일이 parsing된 형태.
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        // SchemaGenerator : TypeDefinitionRegistry와 RuntimeWiring을 결합시켜 GraphQLSchema를 생성
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

    }

    /**
     * DataFetcher 등록
     * @return RuntimeWiring
     */
    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
//                .type(newTypeWiring("Query")
//                        .dataFetcher("employees", graphQLDataFetchers.getEmployees()))
                .type(newTypeWiring("Query")
                        .dataFetcher("employeeByEmpNo", graphQLDataFetchers.getEmployeeByEmpNo()))
                .type(newTypeWiring("Query")
                        .dataFetcher("departments", graphQLDataFetchers.getDepartments()))
                .type(newTypeWiring("Query")
                        .dataFetcher("titles", graphQLDataFetchers.getTitles()))
                .build();
    }


}
