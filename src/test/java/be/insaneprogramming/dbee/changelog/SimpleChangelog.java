package be.insaneprogramming.dbee.changelog;

import org.springframework.jdbc.core.JdbcTemplate;

import be.insaneprogramming.dbee.annotation.ChangeLog;
import be.insaneprogramming.dbee.annotation.ChangeSet;

@ChangeLog
public class SimpleChangelog {
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
