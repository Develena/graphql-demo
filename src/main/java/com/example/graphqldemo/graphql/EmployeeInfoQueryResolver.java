package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.Employee;
import com.example.graphqldemo.entity.EmployeeInfo;
import com.example.graphqldemo.entity.Gender;
import com.example.graphqldemo.mapper.EmployeeMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeInfoQueryResolver implements GraphQLQueryResolver {

    private final EmployeeMapper employeeMapper;

    public EmployeeInfo employeeByEmpNo(int empNo){
        System.out.println("EmployeeInfoQueryResolver - employeeByEmpNo()");
        Employee employee = employeeMapper.selectEmployeeByEmpNo(empNo);
        return EmployeeInfo.builder()
                .empNo(employee.getEmpNo())
                .birthDate(employee.getBirthDate())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .gender(employee.getGender())
                .hireDate(employee.getHireDate())
                .build();
    }

}
