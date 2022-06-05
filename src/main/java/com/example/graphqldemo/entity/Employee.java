package com.example.graphqldemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Employee {
    private Integer empNo;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String gender;
    private String hireDate;

}
