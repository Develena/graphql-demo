package com.example.graphqldemo.mapper;

import com.example.graphqldemo.entity.Employee;
import com.example.graphqldemo.entity.EmployeeInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employees")
    List<Employee> selectAllEmployees();

    @Select("SELECT * FROM employees WHERE emp_no=#{empNo}")
    Employee selectEmployeeByEmpNo(@Param("empNo") int empNo);

}
