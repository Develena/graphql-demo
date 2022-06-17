package com.example.graphqldemo.graphql;

import com.example.graphqldemo.mapper.SalaryMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SalaryQueryResolver implements GraphQLQueryResolver {

    private final SalaryMapper salaryMapper;

}
