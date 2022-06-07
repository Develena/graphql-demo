package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.*;
import com.example.graphqldemo.mapper.DepartmentMapper;
import com.example.graphqldemo.mapper.EmployeeMapper;
import com.example.graphqldemo.mapper.SalaryMapper;
import com.example.graphqldemo.mapper.TitleMapper;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

@Component
public class GraphQLDataFetchers {

    private final EmployeeMapper employeeMapper;
    private final TitleMapper titleMapper;
    private final DepartmentMapper departmentMapper;
    private final SalaryMapper salaryMapper;

    // 의존성 주입
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
            // DataFetching Environment로부터 스키마에 작성된 파라미터를 불러올 수 있다.
            int empNo = Integer.parseInt(dataFetchingEnvironment.getArgument("empNo"));

            EmployeeInfo employeeInfo = employeeMapper.selectEmployeeInfoByEmpNo(empNo); // Join 쿼리
            Title title = titleMapper.selectTitleByEmpNo(empNo);
            Salary salary = salaryMapper.selectLastSalaryByEmpNo(empNo);

            // DepartmentDetail 스키마 객체를 생성
            DepartmentDetail departmentDetail = DepartmentDetail.builder()
                    .deptName(employeeInfo.getDeptName())
                    .fromDate(employeeInfo.getDeptFromDate())
                    .toDate(employeeInfo.getDeptToDate())
                    .build();

            // 최종 리턴인 EmployeeDetail 스키마 객체를 생성
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


    // 간단한 쿼리 조회 예시
    public DataFetcher getDepartments() {
        return dataFetchingEnvironment -> {
            return departmentMapper.selectAllDepartments();
        };
    }

    // 간단한 쿼리 조회 예시
    public DataFetcher getTitles() {
        return dataFetchingEnvironment -> {
            return titleMapper.selectAllTitles();
        };
    }
}
