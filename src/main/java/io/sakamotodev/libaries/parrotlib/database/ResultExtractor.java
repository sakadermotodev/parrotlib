package io.sakamotodev.libaries.parrotlib.database;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultExtractor<T> {
    T extract(ResultSet rs) throws Exception;
}
