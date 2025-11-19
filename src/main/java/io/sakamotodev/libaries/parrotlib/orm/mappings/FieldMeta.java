package io.sakamotodev.libaries.parrotlib.orm.mappings;

import java.lang.reflect.Field;

public class FieldMeta {
    public Field field;
    public String columnName;
    public boolean isId;

    public FieldMeta(Field field, String columnName, boolean isId) {
        this.field = field;
        this.columnName = columnName;
        this.isId = isId;
    }
}
