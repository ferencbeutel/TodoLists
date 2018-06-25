package beutel.ferenc.de.todolists.contact.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.common.domain.DBHelper;
import beutel.ferenc.de.todolists.todo.domain.Todo;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static beutel.ferenc.de.todolists.contact.domain.ContactContract.ContactEntry.ALL_COLUMNS;
import static beutel.ferenc.de.todolists.contact.domain.ContactContract.ContactEntry.TABLE;

public class ContactRepository extends SQLiteOpenHelper {

    private static List<Contact> cursorToContact(final Cursor cursor, final Context context) {
        final List<Contact> fetchedContacts = new ArrayList<>();

        while (cursor.moveToNext()) {
            final int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            final Cursor moreDataCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI},
                    ContactsContract.Contacts._ID + "=?",
                    new String[]{String.valueOf(id)},
                    null);

            if (moreDataCursor != null && moreDataCursor.moveToFirst()) {
                String photoUri = moreDataCursor.getString(moreDataCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                fetchedContacts.add(Contact.builder()
                        ._id(id)
                        .name(moreDataCursor.getString(moreDataCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))
                        .profileImageUri(photoUri != null ? Uri.parse(photoUri) : null)
                        .build());
                moreDataCursor.close();
            }
        }

        cursor.close();
        return fetchedContacts;
    }

    private final Context context;

    public ContactRepository(Context context) {
        super(context, ContactContract.DB_NAME, null, ContactContract.DB_VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DBHelper.CREATE_DB_COMMANDS().forEach(db::execSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DBHelper.UPDATE_DB_COMMANDS().forEach(db::execSQL);
    }

    public Integer insertByUri(final Uri contactUri) {
        final SQLiteDatabase writeableDB = getWritableDatabase();
        Integer contactId = getContactId(contactUri);
        if (contactId != null) {
            writeableDB.insert(TABLE, null, contactToContentValues(Contact.builder()
                    ._id(contactId)
                    .build()));
        }
        writeableDB.close();

        return contactId;
    }

    private Integer getContactId(final Uri contactUri) {
        Cursor idCursor = context.getContentResolver().query(contactUri, null, null, null);

        if (idCursor == null) {
            return null;
        }

        if (idCursor.moveToFirst()) {
            final Integer resultId = idCursor.getInt(idCursor.getColumnIndex(ContactsContract.Contacts._ID));
            idCursor.close();
            return resultId;
        }
        idCursor.close();
        return null;
    }

    private ContentValues contactToContentValues(final Contact contact) {
        final ContentValues values = new ContentValues();
        values.put(_ID, contact.get_id());

        return values;
    }

    public List<Contact> findForTodo(final Todo todo) {
        if (todo.getContactIds().size() == 0) {
            return new ArrayList<>();
        }
        final SQLiteDatabase readableDB = getReadableDatabase();
        List<Contact> result = cursorToContact(readableDB.query(TABLE,
                ALL_COLUMNS,
                _ID + " IN (" + todo.contactIdString() + ")",
                null,
                null,
                null,
                null), context);
        readableDB.close();

        return result;
    }
}
