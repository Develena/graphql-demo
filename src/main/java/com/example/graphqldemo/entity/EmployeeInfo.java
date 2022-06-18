package com.example.graphqldemo.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 직원 테이블의 raw 데이터와
 * 직원의 현재 연봉, 직무의 필드를 가진 객체 정의한다.
 */
@Getter
@Setter
@ToString
@Builder
public class EmployeeInfo {

    private Integer empNo;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String gender;
    private String hireDate;
    // 객체 필드
    private Title title;
    private Salary salary;

}
