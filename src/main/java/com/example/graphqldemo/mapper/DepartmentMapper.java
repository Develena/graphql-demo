package com.example.graphqldemo.mapper;

import com.example.graphqldemo.entity.Department;
import com.example.graphqldemo.entity.DepartmentDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    @Select("SELECT * FROM departments")
    List<Department> selectAllDepartments();

    @Select("SELECT * FROM departments WHERE dept_no=#{deptNo}")
    Department selectDepartmentByDeptNo(@Param("deptNo") String deptNo);

    DepartmentDetail selectDepartmentDetailByDeptNo(String deptNo);


}
