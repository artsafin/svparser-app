package in.artsaf.seriesapp.data;

import android.database.MatrixCursor;

import java.lang.reflect.Field;
import java.util.List;

public class ListCursor extends MatrixCursor {
    public interface ObjectToStringConverter<T> {
        String convert(T object);
    }

    private static <T> String[] getObjectFields(Class<? extends T> cls) {
        Field[] fields = cls.getFields();
        String[] arr = new String[fields.length];

        for (int i = 0; i<fields.length; i++) {
            arr[i] = fields[i].getName();
        }

        return arr;
    }

    public <T> ListCursor(List<T> data, Class<? extends T> cls) {
        super(ListCursor.getObjectFields(cls), data.size());


        for (T item: data) {
            addRow(getRow(item));
        }
    }

    private <T> String[] getRow(T item) {
        Field[] fields = item.getClass().getFields();
        String[] row = new String[fields.length];
        for (int i = 0; i<fields.length; i++) {
            try {
                Object value = fields[i].get(item);
                row[i] = (value != null) ? value.toString() : null;
            } catch (IllegalAccessException e) {
                row[i] = null;
            }
        }

        return row;
    }
}
