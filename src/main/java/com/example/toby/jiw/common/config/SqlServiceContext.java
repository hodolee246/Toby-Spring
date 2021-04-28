package com.example.toby.jiw.common.config;

import com.example.toby.jiw.dao.sql.EmbeddedDbSqlRegistry;
import com.example.toby.jiw.dao.sql.JaxbXmlSqlReader;
import com.example.toby.jiw.dao.sql.OxmSqlService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@Configuration
public class SqlServiceContext {
    // ch7
    @Bean
    public OxmSqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public JaxbXmlSqlReader sqlReader() {
        JaxbXmlSqlReader reader = new JaxbXmlSqlReader();
        reader.setSqlmapFile("/sql/sqlmap.xml");
        return reader;
    }

    @Bean
    public EmbeddedDbSqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(embeddedDatabase());
        return sqlRegistry;
    }

    @Bean
    public Jaxb2Marshaller unmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.toby.jiw.dao.sql.jaxb");
        return marshaller;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("/sql/embedded-db-schema.sql")
                .build();
    }
}
