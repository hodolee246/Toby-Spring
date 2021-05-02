package com.example.toby.jiw.common.config.annotaion;

import com.example.toby.jiw.common.config.SqlServiceContext;
import org.springframework.context.annotation.Import;

@Import(value = SqlServiceContext.class)
public @interface EnableSqlService {
}
