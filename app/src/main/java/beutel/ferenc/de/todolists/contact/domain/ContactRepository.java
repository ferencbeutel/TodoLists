package beutel.ferenc.de.todolists.contact.domain;

import static android.provider.BaseColumns._ID;
import static beutel.ferenc.de.todolists.contact.domain.ContactContract.ContactEntry.ALL_COLUMNS;
import static beutel.ferenc.de.todolists.contact.domain.ContactContract.ContactEntry.TABLE;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import beutel.ferenc.de.todolists.common.domain.DBHelper;
import beutel.ferenc.de.todolists.todo.domain.Todo;

public class ContactRepository extends SQLiteOpenHelper {

  private final Context context;

  public ContactRepository(final Context context) {
    super(context, ContactContract.DB_NAME, null, ContactContract.DB_VERSION);

    this.context = context;
  }

  private static List<Contact> cursorToContact(final Cursor cursor, final Context context) {
    final List<Contact> fetchedContacts = new ArrayList<>();

    while (cursor.moveToNext()) {
      final int id = cursor.getInt(cursor.getColumnIndex(_ID));
      final Cursor moreDataCursor = context.getContentResolver()
        .query(Contacts.CONTENT_URI, new String[]{Contacts.DISPLAY_NAME, Contacts.PHOTO_THUMBNAIL_URI}, _ID + "=?",
          new String[]{String.valueOf(id)}, null);

      if (moreDataCursor != null && moreDataCursor.moveToFirst()) {
        final String photoUri = moreDataCursor.getString(moreDataCursor.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI));
        fetchedContacts.add(Contact.builder()
          ._id(id)
          .name(moreDataCursor.getString(moreDataCursor.getColumnIndex(Contacts.DISPLAY_NAME)))
          .profileImageUri(photoUri != null ? Uri.parse(photoUri) : null)
          .build());
        moreDataCursor.close();
      }
    }

    cursor.close();
    return fetchedContacts;
  }

  @Override
  public void onCreate(final SQLiteDatabase db) {
    DBHelper.CREATE_DB_COMMANDS().forEach(db::execSQL);
  }

  @Override
  public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    DBHelper.UPDATE_DB_COMMANDS().forEach(db::execSQL);
  }

  public Integer insertByUri(final Uri contactUri) {
    final SQLiteDatabase writeableDB = getWritableDatabase();
    final Integer contactId = getContactId(contactUri);
    if (contactId != null) {
      writeableDB.insert(TABLE, null, contactToContentValues(Contact.builder()._id(contactId).build()));
    }
    writeableDB.close();

    return contactId;
  }

  private Integer getContactId(final Uri contactUri) {
    final Cursor idCursor = context.getContentResolver().query(contactUri, null, null, null);

    if (idCursor == null) {
      return null;
    }

    if (idCursor.moveToFirst()) {
      final Integer resultId = idCursor.getInt(idCursor.getColumnIndex(_ID));
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
    if (todo.getContactIds().isEmpty()) {
      return new ArrayList<>();
    }
    final SQLiteDatabase readableDB = getReadableDatabase();
    final List<Contact> result = cursorToContact(
      readableDB.query(TABLE, ALL_COLUMNS, _ID + " IN (" + todo.contactIdString() + ")", null, null, null, null), context);
    readableDB.close();

    return result;
  }
}
