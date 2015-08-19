package be.insaneprogramming.dbee.changelog

import be.insaneprogramming.dbee.annotation.ChangeLog
import be.insaneprogramming.dbee.annotation.ChangeSet
import groovy.sql.Sql

@ChangeLog
class GroovyChangelog {
    @ChangeSet(author = "johndoe", order = 1)
    def changeLog(Sql sql) {
        sql.execute 'CREATE TABLE hello(world VARCHAR(200))'
    }
}
