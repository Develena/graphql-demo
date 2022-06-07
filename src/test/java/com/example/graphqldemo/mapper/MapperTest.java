package com.example.graphqldemo.mapper;

import com.example.graphqldemo.entity.Department;
import com.example.graphqldemo.entity.Employee;
import com.example.graphqldemo.entity.EmployeeInfo;
import com.example.graphqldemo.entity.Title;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;


@ExtendWith(SpringExtension.class) // JUnit5
@MybatisTest // setup test components for testing pure MyBatis component. @Autowired 동작
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Datasource 연결
class MapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private TitleMapper titleMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private SalaryMapper salaryMapper;

    @Test
    public void Employee_Mapper_Test(){
        Assertions.assertNotNull(employeeMapper.selectAllEmployees());
        List<Employee> employeeList = employeeMapper.selectAllEmployees();
        System.out.println("size : "+ employeeList.size());
    }

    @Test
    public void EmployeeInfo_Test(){
        EmployeeInfo employeeInfo = employeeMapper.selectEmployeeInfoByEmpNo(10001);
        System.out.println("emp : " + employeeInfo.toString());

    }

    @Test
    public void Title_Mapper_Test(){
        Assertions.assertNotNull(titleMapper.selectAllTitles());
    }
    @Test
    public void Department_Mapper_Test(){
        Assertions.assertNotNull(departmentMapper.selectAllDepartments());
        List<Department> departmentList = departmentMapper.selectAllDepartments();
        System.out.println("size : "+ departmentList.size());
//        employeeList.forEach(e -> {
//            System.out.println(e.toString());
//        });
    }

    @Test
    public void Salary_Mapper_Test(){
        Assertions.assertNotNull(salaryMapper.selectAllSalaries());

    }
}
