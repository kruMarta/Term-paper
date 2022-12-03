package database;

import global.GlobalVariables;

import java.sql.*;
import java.util.logging.Level;

/**
 * This class connects program to MySQL database
 */
public class DBConnection {

    public static Connection connectDB(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:/DBname", "user", "password");
            GlobalVariables.logger.info("Database was connected successfully!");
        }
        catch (Exception e){
            GlobalVariables.logger.log(Level.WARNING,"Can`t connect database" + e);
            e.printStackTrace();
        }
        return connection;
    }
}
