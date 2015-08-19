package be.insaneprogramming.dbee;

import org.hsqldb.jdbcDriver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.util.Arrays;
import java.util.UUID;

import javax.sql.DataSource;

import be.insaneprogramming.dbee.annotation.ChangeLog;
import be.insaneprogramming.dbee.annotation.ChangeSet;
import be.insaneprogramming.dbee.exception.DBeeChangeSetExecutionException;

public class FailOnErrorTest {

    private DBee dBee;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void initDataSource() {
        DataSource dataSource = new SimpleDriverDataSource(new jdbcDriver(), "jdbc:hsqldb:mem:" + UUID.randomUUID().toString());
        dBee = new DBee(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test(expected = DBeeChangeSetExecutionException.class)
    public void shoudldNotExecuteMigration() {
        dBee.setChangeLogClasses(Arrays.asList(FailingChangelog.class));
        dBee.execute();
    }

    @ChangeLog
    public static class FailingChangelog {
        @ChangeSet(author = "johndoe", order = 1)
        public void changeOne(JdbcTemplate template) {
            String query = "CREATE TABLE hello(world VARCHAR(200))";
            template.execute(query);
        }

        @ChangeSet(author = "johndoe", order = 2)
        public void changeTwo(JdbcTemplate template) {
            String query = "INSERT INTO hello VALUE('hello there')";
            template.execute(query);
        }
    }
}
