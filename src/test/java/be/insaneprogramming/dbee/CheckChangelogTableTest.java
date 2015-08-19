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

/**
 * Created by lievendoclo on 19/08/15.
 */
public class CheckChangelogTableTest {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @Before
    public void initDataSource() {
        dataSource = new SimpleDriverDataSource(new jdbcDriver(), "jdbc:hsqldb:mem:" + UUID.randomUUID().toString());
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void shouldExecuteMigration() {
        DBee dBee = new DBee(dataSource);
        dBee.setChangeLogClasses(Arrays.asList(SimpleChangelog.class));
        dBee.execute();

        List<String> result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(1));

        dBee = new DBee(dataSource);
        dBee.setChangeLogClasses(Arrays.asList(SimpleChangelog.class));
        dBee.execute();

        result = jdbcTemplate.queryForList("SELECT world FROM hello", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(1));
    }

    @ChangeLog
    public static class SimpleChangelog {
        @ChangeSet(author = "johndoe", order = 1)
        public void changeOne(JdbcTemplate template) {
            String query = "CREATE TABLE hello(world VARCHAR(200))";
            template.execute(query);
        }

        @ChangeSet(author = "johndoe", order = 2)
        public void changeTwo(JdbcTemplate template) {
            String query = "INSERT INTO hello VALUES('hello there')";
            template.execute(query);
        }
    }
}
