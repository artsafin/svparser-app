package in.artsaf.seriesapp.data.api;

import android.database.CursorIndexOutOfBoundsException;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import in.artsaf.seriesapp.data.ListCursor;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ListCursorTest {

    private class ValueObject {
        public long id;
        public String name;

        public ValueObject(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private class ValueObjectWithPrivate {
        public long id;
        protected String surname;
        private String name;

        public ValueObjectWithPrivate(long id) {
            this.id = id;
            this.name = "set in constructor";
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void commonUsecase() {
        List<ValueObject> list = Arrays.asList(
                new ValueObject(1, "first"),
                new ValueObject(2, "second"),
                new ValueObject(3, "third")
        );

        ListCursor cursor = new ListCursor(list, ValueObject.class);

        assertEquals(3, cursor.getCount());
        assertEquals(2, cursor.getColumnCount());

        assertTrue(cursor.moveToNext());
        assertEquals(1L, cursor.getLong(0));
        assertEquals("first", cursor.getString(1));

        assertTrue(cursor.moveToLast());
        assertEquals(3L, cursor.getLong(0));
        assertEquals("third", cursor.getString(1));
    }

    @Test
    public void valueNull() {
        List<ValueObject> list = Arrays.asList(
                new ValueObject(0, null)
        );

        ListCursor cursor = new ListCursor(list, ValueObject.class);

        assertEquals(1, cursor.getCount());
        assertEquals(2, cursor.getColumnCount());

        assertTrue(cursor.moveToNext());
        assertEquals(0L, cursor.getLong(0));
        assertEquals(null, cursor.getString(1));
    }

    @Test
    public void fieldPrivate() {
        List<ValueObjectWithPrivate> list = Arrays.asList(
                new ValueObjectWithPrivate(0)
        );

        ListCursor cursor = new ListCursor(list, ValueObjectWithPrivate.class);

        assertEquals(1, cursor.getCount());
        assertEquals(1, cursor.getColumnCount());

        assertTrue(cursor.moveToNext());

        assertEquals(0L, cursor.getLong(0));

        boolean exception = false;
        try {
            assertEquals(null, cursor.getString(1));
        } catch (CursorIndexOutOfBoundsException exc) {
            exception = true;
        }
        if (!exception) {
            fail("Expected exception: CursorIndexOutOfBoundsException");
        }
    }

    @Test
    public void listEmpty() {
        List<ValueObject> list = Arrays.asList();

        ListCursor cursor = new ListCursor(list, ValueObject.class);

        assertEquals(0, cursor.getCount());
        assertEquals(2, cursor.getColumnCount());

        assertFalse(cursor.moveToNext());
    }
}