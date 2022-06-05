package com.example.graphqldemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class EmployeeInfo {

    private Integer empNo;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String gender;
    private String hireDate;
    // Department 정보는 dept_emp 테이블에서 현재 dept 정보를 먼저 조회해야한다.
    // 이는 join 을 통해 데이터를 가져온다.
    // 후에 DataFetcher에서 Department 정보를 세팅한다.
    private String deptNo;
    private String deptName;
    private String deptFromDate;
    private String deptToDate;

}
