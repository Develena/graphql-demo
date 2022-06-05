package com.example.graphqldemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeptManager {

    private Integer empNo;
    private String deptNo;
    private String fromDate;
    private String toDate;
}
