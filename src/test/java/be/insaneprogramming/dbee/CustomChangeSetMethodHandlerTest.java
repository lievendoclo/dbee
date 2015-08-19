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
import be.insaneprogramming.dbee.exception.DBeeChangeSetException;
import be.insaneprogramming.dbee.handler.IntegerChangeSetMethodHandler;

/**
 * Created by lievendoclo on 19/08/15.
 */
public class CustomChangeSetMethodHandlerTest {
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
        dBee.addChangeSetMethodHandler(IntegerChangeSetMethodHandler.class);
        dBee.execute();

        List<String> result = jdbcTemplate.queryForList("SELECT author FROM dbeechangelog", String.class);
        Assert.assertThat(result.size(), CoreMatchers.is(1));
    }

    @Test(expected = DBeeChangeSetException.class)
    public void shouldFailDueToMissingHandler() {
        dBee.setChangeLogClasses(Arrays.asList(SimpleChangelog.class));
        dBee.execute();
    }

    @ChangeLog
    public static class SimpleChangelog {
        @ChangeSet(author = "johndoe", order = 1)
        public void changeOne(Integer value) {

        }
    }
}
