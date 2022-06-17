package com.example.graphqldemo.graphql;

import com.example.graphqldemo.entity.Title;
import com.example.graphqldemo.mapper.TitleMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  Query 스키마에 해당하는 메서드를 GraphQLQueryResolver를 상속한 클래스에 작성.
 */
@Component
@RequiredArgsConstructor // 생성자 주입: final 혹은 @NotNull 이 붙은 필드의 생성자를 자동 생성
public class TitleQueryResolver implements GraphQLQueryResolver {
    private final TitleMapper titleMapper;

    // schema.graphqls의 Title과 Java 타입의 이름이 꼭 일치하지 않아도 됨.
    public List<Title> getTitles(){
        return titleMapper.selectAllTitles();
    }


}
