package com.example.graphqldemo.mapper;

import com.example.graphqldemo.entity.Salary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SalaryMapper {

    @Select("SELECT * FROM salaries")
    List<Salary> selectAllSalaries();

    @Select("SELECT * FROM salaries WHERE emp_no=#{empNo} order by to_date desc limit 1")
    Salary selectCurrentSalaryByEmpNo(@Param("empNo") int empNo);


}
