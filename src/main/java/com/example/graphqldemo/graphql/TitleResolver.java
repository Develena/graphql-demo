package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.EmployeeInfo;
import com.example.graphqldemo.entity.Salary;
import com.example.graphqldemo.entity.Title;
import com.example.graphqldemo.mapper.EmployeeMapper;
import com.example.graphqldemo.mapper.SalaryMapper;
import com.example.graphqldemo.mapper.TitleMapper;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Field Resolver 구현
 * 타입 내에 또 다른 타입이 필드로 설정한 경우, 혹은 DB 쿼리 후 복잡한 계산 값이 추가로 필요한 경우 구현
 */
@Component
@RequiredArgsConstructor
public class TitleResolver implements GraphQLResolver<EmployeeInfo> {

    private final TitleMapper titleMapper;

    public Title title(EmployeeInfo emp) {
        System.out.println("Request Title for EmployeeInfo");
        return titleMapper.selectCurrentTitleByEmpNo(emp.getEmpNo());
    }

}
