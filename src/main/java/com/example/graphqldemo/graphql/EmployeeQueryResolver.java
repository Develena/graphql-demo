package com.example.graphqldemo.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.example.graphqldemo.entity.EmployeeDetail;
import com.example.graphqldemo.entity.EmployeeInfo;
import com.example.graphqldemo.mapper.EmployeeMapper;


public class EmployeeQueryResolver implements GraphQLQueryResolver {
    private final EmployeeMapper employeeMapper;

    public EmployeeQueryResolver(EmployeeMapper employeeMapper){
        this.employeeMapper = employeeMapper;
    }

//    public EmployeeDetail getEmployeeByEmpNo(String empNo){
//        EmployeeInfo employeeInfo = employeeMapper.selectEmployeeInfoByEmpNo(Integer.parseInt(empNo));
//        return from(employeeInfo);
//    }

//    private EmployeeDetail from(EmployeeDetail employeeDetail){
//        return EmployeeDetail.builder().build();
//    }
}
