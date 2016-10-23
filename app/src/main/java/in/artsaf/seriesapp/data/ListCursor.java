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

    private <T> Object[] getRow(T item) {
        Field[] fields = item.getClass().getFields();
        Object[] row = new Object[fields.length];
        for (int i = 0; i<fields.length; i++) {
            try {
                row[i] = fields[i].get(item);
            } catch (IllegalAccessException e) {
                row[i] = null;
            }
        }

        return row;
    }
}
