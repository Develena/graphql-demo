package com.example.graphqldemo.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.example.graphqldemo.mapper.DepartmentMapper;


public class DepartmentQueryResolver implements GraphQLQueryResolver {
    private final DepartmentMapper departmentMapper;

    public DepartmentQueryResolver(DepartmentMapper departmentMapper){
        this.departmentMapper = departmentMapper;
    }
}
