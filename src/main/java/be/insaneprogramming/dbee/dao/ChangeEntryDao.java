package be.insaneprogramming.dbee.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import be.insaneprogramming.dbee.model.ChangeEntry;

public class ChangeEntryDao {
    private static final Logger logger = LoggerFactory.getLogger(ChangeEntryDao.class);

    public static final String TABLE_NAME = "dbeechangelog";

    private JdbcTemplate jdbcTemplate;

    public ChangeEntryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        if(!changelogTableExists(jdbcTemplate)) {
            logger.info("dbeechangelog table does not exist, creating");
            createChangelogTable(jdbcTemplate);
        }
    }

    private boolean changelogTableExists(JdbcTemplate jdbcTemplate) {
        String checkQuery = "SELECT 1 from " + TABLE_NAME + " LIMIT 1";
        try {
            jdbcTemplate.execute(checkQuery);
            return true;
        } catch (DataAccessException dataAccessException) {
            return false;
        }
    }

    private void createChangelogTable(JdbcTemplate jdbcTemplate) {
        String
            createQuery =
            "CREATE TABLE " + TABLE_NAME + "(author VARCHAR(500), timestamp BIGINT, class VARCHAR(500), method VARCHAR(500), PRIMARY KEY(class, method))";
        jdbcTemplate.execute(createQuery);
    }


    public boolean isNewChange(ChangeEntry changeEntry) {
        String query = "SELECT count(1) FROM " + TABLE_NAME + " WHERE class = ? AND method = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{changeEntry.getChangeLogClass(), changeEntry.getChangeSetMethodName()}, Integer.class) == 0;
    }

    public boolean save(ChangeEntry changeEntry) {
        String query = "INSERT INTO " + TABLE_NAME + "(author, timestamp, class, method) values (?, ?, ?, ?);";
        Object[]
            params =
            new Object[]{changeEntry.getAuthor(), changeEntry.getTimestamp(), changeEntry.getChangeLogClass(),
                         changeEntry.getChangeSetMethodName()};
        return jdbcTemplate.update(query, params) > 0;
    }

}

