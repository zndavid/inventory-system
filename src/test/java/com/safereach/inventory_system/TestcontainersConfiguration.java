package com.safereach.inventory_system;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

//    @Bean
//    PostgreSQLContainer<?> postgres() {
//        var c = new PostgreSQLContainer<>(
//                DockerImageName.parse("postgis/postgis:16-3.4-alpine")
//                        .asCompatibleSubstituteFor("postgres"));
//        c.start();
//        return c;
//    }
//
//    @Bean
//    DataSource dataSource(PostgreSQLContainer<?> c) {
//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(c.getJdbcUrl());
//        ds.setUsername(c.getUsername());
//        ds.setPassword(c.getPassword());
//        ds.setDriverClassName("org.postgresql.Driver");
//        return ds;
//    }
}
