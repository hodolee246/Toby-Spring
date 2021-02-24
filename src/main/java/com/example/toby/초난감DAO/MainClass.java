package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.daofactory.DaoFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MainClass {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        // DaoFactory
//        DaoFactory daoFactory = new DaoFactory();
//
//        UserDaoJdbc userDao1 = daoFactory.userDao();
//        UserDaoJdbc userDao2 = daoFactory.userDao();
//        log.info("userDao: '{}'", userDao1);
//        log.info("userDao: '{}'", userDao2);
//        // ApplicationContext
//        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);
//
//        UserDaoJdbc userDao3 = applicationContext.getBean("userDao", UserDaoJdbc.class);
//        UserDaoJdbc userDao4 = applicationContext.getBean("userDao", UserDaoJdbc.class);
//        log.info("userDao: '{}'", userDao3);
//        log.info("userDao: '{}'", userDao4);

        TypeService typeService = new TypeService();
        typeService.test();
    }
}
@AllArgsConstructor
class TestDTO {
    String a;
    String b;
    String c;
}
class TypeService {
    List<TestDTO> arrayList = new ArrayList();
    public void test() {
        arrayList.add(new TestDTO("abc1", "bbc1", "ccc1"));
        arrayList.add(new TestDTO("abc2", "bbc2", "ccc2"));
        arrayList.add(new TestDTO("abc3", "bbc3", "ccc3"));
        arrayList.add(new TestDTO("abc1", "bbc1", "ccc1"));
        arrayList.add(new TestDTO("abc2", "bbc2", "ccc2"));
        arrayList.add(new TestDTO("abc3", "bbc3", "ccc3"));


        List<String> list1 = arrayList.stream().map(t -> t.a).filter("abc1"::equals).collect(Collectors.toList());
        List<String> list2 = arrayList.stream().map(testDTO -> testDTO.a).filter("abc2"::equals).collect(Collectors.toList());
        List<String> list3 = arrayList.stream().map(testDTO -> testDTO.a).filter("abc3"::equals).collect(Collectors.toList());

        System.out.println(list1.size());
        System.out.println(list2.size());
        System.out.println(list3.size());
    }

}
