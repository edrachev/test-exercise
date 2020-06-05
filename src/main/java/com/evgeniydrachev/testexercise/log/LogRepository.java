package com.evgeniydrachev.testexercise.log;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class LogRepository {

    private static final String DDL_SQL = "CREATE TABLE LOG (\n" +
            "  id INT AUTO_INCREMENT  PRIMARY KEY,\n" +
            "  text VARCHAR(500) NOT NULL,\n" +
            "  from_lang VARCHAR(20) NOT NULL,\n" +
            "  to_lang VARCHAR(20) NOT NULL,\n" +
            "  when TIMESTAMP NOT NULL,\n" +
            "  ip VARCHAR(39) NOT NULL\n" +
            ");";
    private static final String INSERT_SQL = "INSERT INTO LOG (text, from_lang, to_lang, when, ip) VALUES(?, ?, ?, ?, ?)";
    private static final String READ_SQL = "SELECT text, from_lang, to_lang, when, ip FROM LOG";

    private final String url;
    private final String login;
    private final String password;

    private final Connection connection;

    public LogRepository(@Value("${jdbc.url}") String url, @Value("${jdbc.username}") String login, @Value("${jdbc.password}") String password) throws SQLException {
        this.url = url;
        this.login = login;
        this.password = password;
        this.connection = DriverManager.getConnection(url, login, password);
        PreparedStatement statement = connection.prepareStatement(DDL_SQL);
        statement.execute();
    }

    @PreDestroy
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void add(Log log) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_SQL);
            statement.setString(1, log.getText());
            statement.setString(2, log.getFrom());
            statement.setString(3, log.getTo());
            statement.setTimestamp(4, new Timestamp(log.getWhen().toEpochMilli()));
            statement.setString(5, log.getText());
            statement.execute();
        } catch (SQLException throwables) {
            throw new RuntimeException("Cannot log action", throwables);
        }
    }

    public List<Log> readAll() {
        List<Log> logs = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(READ_SQL);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String text = resultSet.getString(1);
                String from = resultSet.getString(2);
                String to = resultSet.getString(3);
                Timestamp when = resultSet.getTimestamp(4);
                String ip = resultSet.getString(5);
                Log log = new Log(text, from, to, when.toInstant(), ip);
                logs.add(log);
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("Cannot read logs", throwables);
        }

        return logs;
    }
}
