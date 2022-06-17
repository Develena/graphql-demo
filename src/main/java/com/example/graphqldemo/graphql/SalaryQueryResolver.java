package com.example.graphqldemo.graphql;

import com.example.graphqldemo.mapper.SalaryMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class SalaryQueryResolver implements GraphQLQueryResolver {

    private final SalaryMapper salaryMapper;

    public SalaryQueryResolver(SalaryMapper salaryMapper){
        this.salaryMapper = salaryMapper;
    }



}
