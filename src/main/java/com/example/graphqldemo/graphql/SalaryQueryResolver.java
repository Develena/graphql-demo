package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.Salary;
import com.example.graphqldemo.entity.Title;
import com.example.graphqldemo.mapper.SalaryMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SalaryQueryResolver implements GraphQLQueryResolver {

    private final SalaryMapper salaryMapper;

//    public List<Salary> getSalaries(){
//        return salaryMapper.selectAllSalaries();
//    }

}
