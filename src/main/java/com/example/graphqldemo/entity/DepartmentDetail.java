package com.example.graphqldemo.entity;

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
public class DepartmentDetail {

    private String deptName;
    private String fromDate;
    private String toDate;

}
