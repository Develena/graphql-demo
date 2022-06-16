package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.Department;
import com.example.graphqldemo.entity.Title;
import com.example.graphqldemo.mapper.DepartmentMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentQueryResolver implements GraphQLQueryResolver {
    private final DepartmentMapper departmentMapper;

    public DepartmentQueryResolver(DepartmentMapper departmentMapper){
        this.departmentMapper = departmentMapper;
    }

    public List<Department> getDepartments(){
        return departmentMapper.selectAllDepartments();
    }
}
