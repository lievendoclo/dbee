package be.insaneprogramming.dbee;

import org.hamcrest.CoreMatchers;
import org.hsqldb.jdbcDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import be.insaneprogramming.dbee.annotation.ChangeLog;
import be.insaneprogramming.dbee.annotation.ChangeSet;
import be.insaneprogramming.dbee.exception.DBeeChangeSetExecutionException;

public class RunAlwaysTest {

    private DBee dBee;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void initDataSource() {
        DataSource dataSource = new SimpleDriverDataSource(new jdbcDriver(), "jdbc:hsqldb:mem:" + UUID.randomUUID().toString());
        dBee = new DBee(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void shouldRunSecondChangeSetAlways() {
        dBee.setChangeLogClasses(Arrays.asList(RunAlwaysChangelog.class));
        dBee.execute();

        List<String> result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(1));

        dBee.execute();

        result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(2));
    }

    @Test(expected = DBeeChangeSetExecutionException.class)
    public void shouldFailOnSecondRun() {
        dBee.setChangeLogClasses(Arrays.asList(FailingRunAlwaysChangelog.class));
        dBee.execute();

        List<String> result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(1));

        dBee.execute();

        result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(2));
    }

    @Test
    public void shouldIgnoreFailureOnSecondRun() {
        dBee.setChangeLogClasses(Arrays.asList(IgnoredFailingRunAlwaysChangelog.class));
        dBee.execute();

        List<String> result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(1));

        dBee.execute();

        result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(2));
    }

    @ChangeLog
    public static class RunAlwaysChangelog {
        @ChangeSet(author = "johndoe", order = 1)
        public void changeOne(JdbcTemplate template) {
            String query = "CREATE TABLE hello(world VARCHAR(200))";
            template.execute(query);
        }

        @ChangeSet(author = "johndoe", order = 2, runAlways = true)
        public void changeTwo(JdbcTemplate template) {
            String query = "INSERT INTO hello VALUES('hello there')";
            template.execute(query);
        }

        @ChangeSet(author = "johndoe", order = 3, runAlways = true, failOnError = false)
        public void changeThree(JdbcTemplate template) {
            String query = "INSERT INTO hello VALUE('hello there')";
            template.execute(query);
        }
    }

    @ChangeLog
    public static class FailingRunAlwaysChangelog {
        @ChangeSet(author = "johndoe", order = 1, runAlways = true)
        public void changeOne(JdbcTemplate template) {
            String query = "CREATE TABLE hello(world VARCHAR(200))";
            template.execute(query);
        }

        @ChangeSet(author = "johndoe", order = 2, runAlways = true)
        public void changeTwo(JdbcTemplate template) {
            String query = "INSERT INTO hello VALUES('hello there')";
            template.execute(query);
        }
    }

    @ChangeLog
    public static class IgnoredFailingRunAlwaysChangelog {
        @ChangeSet(author = "johndoe", order = 1, runAlways = true, failOnError = false)
        public void changeOne(JdbcTemplate template) {
            String query = "CREATE TABLE hello(world VARCHAR(200))";
            template.execute(query);
        }

        @ChangeSet(author = "johndoe", order = 2, runAlways = true)
        public void changeTwo(JdbcTemplate template) {
            String query = "INSERT INTO hello VALUES('hello there')";
            template.execute(query);
        }
    }
}
