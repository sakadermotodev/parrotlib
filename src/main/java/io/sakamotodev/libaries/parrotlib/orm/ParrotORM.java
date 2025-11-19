package io.sakamotodev.libaries.parrotlib.orm;

import io.sakamotodev.libaries.parrotlib.database.ParrotDatabase;
import io.sakamotodev.libaries.parrotlib.orm.annotations.DBField;
import io.sakamotodev.libaries.parrotlib.orm.annotations.DBId;
import io.sakamotodev.libaries.parrotlib.orm.annotations.DBTable;
import io.sakamotodev.libaries.parrotlib.orm.mappings.FieldMeta;
import io.sakamotodev.libaries.parrotlib.orm.mappings.TableMeta;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

public class ParrotORM {

    private final ParrotDatabase db;

    public ParrotORM(ParrotDatabase db) {
        this.db = db;
    }

    public <T> TableMeta analyze(Class<T> clazz) {
        TableMeta meta = new TableMeta();

        DBTable tableAnn = clazz.getAnnotation(DBTable.class);
        if (tableAnn == null)
            throw new RuntimeException("Missing @DBTable for " + clazz.getName());

        meta.tableName = tableAnn.value();

        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);

            DBField fieldAnn = f.getAnnotation(DBField.class);
            if (fieldAnn != null) {
                boolean isId = f.isAnnotationPresent(DBId.class);

                FieldMeta fm = new FieldMeta(f, fieldAnn.value(), isId);
                if (isId) {
                    meta.idField = fm;
                }
                meta.fields.add(fm);
            }
        }

        if (meta.idField == null)
            throw new RuntimeException("No @DBId field found in " + clazz.getName());

        return meta;
    }

    public <T> void createTable(Class<T> clazz) {
        TableMeta meta = analyze(clazz);

        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(meta.tableName).append(" (");

        for (FieldMeta f : meta.fields) {
            sb.append(f.columnName).append(" ");

            if (f.field.getType() == int.class) sb.append("INT");
            else if (f.field.getType() == String.class) sb.append("TEXT");
            else if (f.field.getType() == java.util.UUID.class) sb.append("VARCHAR(36)");
            else sb.append("TEXT");

            if (f.isId) sb.append(" PRIMARY KEY");

            sb.append(", ");
        }

        String sql = sb.substring(0, sb.length() - 2) + ")";

        db.update(sql);
    }

    public <T> CompletableFuture<T> load(Class<T> clazz, Object id) {
        TableMeta meta = analyze(clazz);

        String sql = "SELECT * FROM " + meta.tableName +
                " WHERE " + meta.idField.columnName + "=?";

        return db.queryAsync(sql, rs -> {
            if (!rs.next()) return null;

            T instance = clazz.getDeclaredConstructor().newInstance();

            for (FieldMeta f : meta.fields) {
                Object value = rs.getObject(f.columnName);
                f.field.set(instance, value);
            }

            return instance;

        }, id);
    }

    public <T> void save(T entity) {
        Class<?> clazz = entity.getClass();
        TableMeta meta = analyze(clazz);

        try {
            Object idValue = meta.idField.field.get(entity);

            // Build SQL
            StringBuilder sb = new StringBuilder("INSERT INTO ")
                    .append(meta.tableName).append(" (");

            StringBuilder placeholders = new StringBuilder();
            Object[] values = new Object[meta.fields.size()];
            int index = 0;

            for (FieldMeta f : meta.fields) {
                sb.append(f.columnName).append(",");
                placeholders.append("?,");
                values[index++] = f.field.get(entity);
            }

            String sql = sb.substring(0, sb.length() - 1) + ") VALUES (" +
                    placeholders.substring(0, placeholders.length() - 1) +
                    ") ON CONFLICT(" + meta.idField.columnName + ") DO UPDATE SET ";

            for (FieldMeta f : meta.fields) {
                if (!f.isId)
                    sql += f.columnName + "=?,";
            }

            sql = sql.substring(0, sql.length() - 1);

            // Create final values array (insert + update)
            Object[] finalValues = new Object[index + (meta.fields.size() - 1)];
            System.arraycopy(values, 0, finalValues, 0, index);

            int uidx = index;
            for (FieldMeta f : meta.fields) {
                if (!f.isId)
                    finalValues[uidx++] = f.field.get(entity);
            }

            db.update(sql, finalValues);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
