package com.example.graphqldemo.entity;

import com.example.graphqldemo.entity.Department;
import com.example.graphqldemo.entity.Salary;
import com.example.graphqldemo.entity.Title;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * GraphQL Schema
 */
@Getter
@Setter
@ToString
@Builder
public class EmployeeDetail {

    private Integer empNo;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String gender;
    private String hireDate;
    private DepartmentDetail dept;
    private Title title;
    private Salary salary;

}
