package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.Employee;
import com.example.graphqldemo.mapper.EmployeeMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeQueryResolver implements GraphQLQueryResolver {

    private final EmployeeMapper employeeMapper;

    public List<Employee> getEmployees(){
        return employeeMapper.selectAllEmployees();
    }

}
