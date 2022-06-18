package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.Department;
import com.example.graphqldemo.mapper.DepartmentMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DepartmentQueryResolver implements GraphQLQueryResolver {
    private final DepartmentMapper departmentMapper;

    public List<Department> departments(){
        System.out.println("DepartmentQueryResolver - getDepartments()");
        return departmentMapper.selectAllDepartments();
    }
}
