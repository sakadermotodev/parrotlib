package io.sakamotodev.libaries.parrotlib.orm.mappings;

import java.util.ArrayList;
import java.util.List;

public class TableMeta {
    public String tableName;
    public FieldMeta idField;
    public List<FieldMeta> fields = new ArrayList<>();
}
