package com.example.graphqldemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Salary {

    private Integer empNo;
    private Integer salary;
    private String fromDate;
    private String toDate;

}
