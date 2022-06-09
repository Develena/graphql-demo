package com.example.graphqldemo.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.example.graphqldemo.mapper.TitleMapper;


public class TitleQueryResolver implements GraphQLQueryResolver {
    private final TitleMapper titleMapper;

    public TitleQueryResolver(TitleMapper titleMapper){
        this.titleMapper = titleMapper;
    }
}
