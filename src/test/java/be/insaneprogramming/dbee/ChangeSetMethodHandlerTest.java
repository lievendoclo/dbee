package be.insaneprogramming.dbee;

import org.hamcrest.CoreMatchers;
import org.hsqldb.jdbcDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import be.insaneprogramming.dbee.annotation.ChangeLog;
import be.insaneprogramming.dbee.annotation.ChangeSet;

public class ChangeSetMethodHandlerTest {

    private DBee dBee;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void initDataSource() {
        DataSource dataSource = new SimpleDriverDataSource(new jdbcDriver(), "jdbc:hsqldb:mem:" + UUID.randomUUID().toString());
        dBee = new DBee(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void shouldExecuteMigration() {
        dBee.setChangeLogClasses(Arrays.asList(SimpleChangelog.class));
        dBee.execute();

        List<String> result = jdbcTemplate.queryForList("SELECT author FROM dbeechangelog", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(3));
    }

    @ChangeLog
    public static class SimpleChangelog {
        @ChangeSet(author = "johndoe", order = 1)
        public void changeOne(JdbcTemplate template) {
            String query = "CREATE TABLE hello(world VARCHAR(200))";
            template.execute(query);
        }

        @ChangeSet(author = "johndoe", order = 2)
        public void changeTwo(DataSource dataSource) {
            String query = "INSERT INTO hello VALUES('hello there')";
            try(Connection connection = dataSource.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    statement.execute(query);
                }
            } catch(SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        @ChangeSet(author = "johndoe", order = 3)
        public void changeThree() {

        }
    }
}
