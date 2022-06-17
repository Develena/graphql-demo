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

    @Select("SELECT * FROM titles WHERE emp_no=#{empNo} order by to_date desc limit 1")
    Title selectCurrentTitleByEmpNo(@Param("empNo") int empNo);



}
