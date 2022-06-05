package com.example.graphqldemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Title {

    private Integer empNo;
    private String title;
    private String fromDate;
    private String toDate;
}
