package com.example.toby.jiw.jaxb;

import com.example.toby.jiw.dao.sql.jaxb.SqlType;
import com.example.toby.jiw.dao.sql.jaxb.Sqlmap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.List;

public class JaxbTest {

    @Test
    public void readSqlmap() throws JAXBException, IOException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        // dir : resource/sql/sqlmap.xml
        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("/sql/sqlmap.xml"));
        List<SqlType> sqlList = sqlmap.getSql();

        Assertions.assertEquals(sqlList.size(), 6);
        Assertions.assertEquals(sqlList.get(0).getKey(), "userAdd");
        Assertions.assertEquals(sqlList.get(0).getValue(), "insert into users(id, name, pwd, level, login, recommend, email) values(?, ?, ?, ?, ?, ?, ?)");
        Assertions.assertEquals(sqlList.get(1).getKey(), "userGet");
        Assertions.assertEquals(sqlList.get(1).getValue(), "select * from users where id = ?");
        Assertions.assertEquals(sqlList.get(2).getKey(), "userGetAll");
        Assertions.assertEquals(sqlList.get(2).getValue(), "select * from users order by id");
        Assertions.assertEquals(sqlList.get(3).getKey(), "userDeleteAll");
        Assertions.assertEquals(sqlList.get(3).getValue(), "delete from users");
        Assertions.assertEquals(sqlList.get(4).getKey(), "userGetCount");
        Assertions.assertEquals(sqlList.get(4).getValue(), "select count(*) from users");
        Assertions.assertEquals(sqlList.get(5).getKey(), "userUpdate");
        Assertions.assertEquals(sqlList.get(5).getValue(), "update users set name = ?, pwd = ?, level = ?, login = ?, recommend = ?, email = ? where id = ?");
    }
}
