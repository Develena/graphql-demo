package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.*;
import com.example.graphqldemo.mapper.DepartmentMapper;
import com.example.graphqldemo.mapper.EmployeeMapper;
import com.example.graphqldemo.mapper.SalaryMapper;
import com.example.graphqldemo.mapper.TitleMapper;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GraphQLDataFetchers {

    private final EmployeeMapper employeeMapper;
    private final TitleMapper titleMapper;
    private final DepartmentMapper departmentMapper;
    private final SalaryMapper salaryMapper;

    public GraphQLDataFetchers(EmployeeMapper employeeMapper
            , TitleMapper titleMapper
            , DepartmentMapper departmentMapper
            , SalaryMapper salaryMapper) {
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
        this.titleMapper = titleMapper;
        this.salaryMapper = salaryMapper;
    }


//    public DataFetcher getEmployees() {
//        return dataFetchingEnvironment -> {
//
//        };
//
//    }


    public DataFetcher getEmployeeByEmpNo() {
        return dataFetchingEnvironment -> {
            int empNo = Integer.parseInt(dataFetchingEnvironment.getArgument("empNo"));
            EmployeeInfo employeeInfo = employeeMapper.selectEmployeeInfoByEmpNo(empNo);
            Title title = titleMapper.selectTitleByEmpNo(empNo);
            Salary salary = salaryMapper.selectLastSalaryByEmpNo(empNo);
            DepartmentDetail departmentDetail = DepartmentDetail.builder()
                    .deptName(employeeInfo.getDeptName())
                    .fromDate(employeeInfo.getDeptFromDate())
                    .toDate(employeeInfo.getDeptToDate())
                    .build();

            return EmployeeDetail.builder()
                    .empNo(empNo)
                    .firstName(employeeInfo.getFirstName())
                    .lastName(employeeInfo.getLastName())
                    .gender(employeeInfo.getGender())
                    .birthDate(employeeInfo.getBirthDate())
                    .hireDate(employeeInfo.getHireDate())
                    .dept(departmentDetail)
                    .salary(salary)
                    .title(title)
                    .build();
        };
    }


    public DataFetcher getDepartments() {
        return dataFetchingEnvironment -> {
            return departmentMapper.selectAllDepartments();
        };
    }

    public DataFetcher getTitles() {
        return dataFetchingEnvironment -> {
            return titleMapper.selectAllTitles();
        };
    }
}
