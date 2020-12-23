package com.example.toby.ch1;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {

    Connection getConnection() throws ClassNotFoundException, SQLException ;
}
