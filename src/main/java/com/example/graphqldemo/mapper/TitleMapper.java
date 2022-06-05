package com.example.graphqldemo.mapper;


import com.example.graphqldemo.entity.Title;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TitleMapper {

    @Select("SELECT * FROM titles")
    List<Title> selectAllTitles();

    @Select("SELECT * FROM titles WHERE emp_no=#{empNo}")
    Title selectTitleByEmpNo(@Param("empNo") int empNo);



}
