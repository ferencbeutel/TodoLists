package beutel.ferenc.de.todolists.contact.view;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.R.layout;
import beutel.ferenc.de.todolists.contact.domain.Contact;

public class ContactArrayAdapter extends ArrayAdapter<Contact> {

  private final Context context;
  private final List<Contact> contacts;

  public ContactArrayAdapter(final Context context, final List<Contact> contacts) {
    super(context, 0, contacts);
    this.context = context;
    this.contacts = contacts;
  }

  @NonNull
  @Override
  public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
    final View listItem =
      convertView == null ? LayoutInflater.from(context).inflate(layout.item_contact, parent, false) : convertView;

    final Contact currentContact = contacts.get(position);

        final QuickContactBadge quickContactBadge = listItem.findViewById(R.id.quick_contact_badge);
        quickContactBadge.assignContactUri(currentContact.getContactUri());
        quickContactBadge.setImageBitmap(currentContact.profileImageBitmap(context));

        final TextView contactNameView = listItem.findViewById(R.id.contact_name);
        contactNameView.setText(currentContact.getName());

        final TextView contactIdTextView = listItem.findViewById(R.id.contact_id);
        contactIdTextView.setText(currentContact.get_id());

        return listItem;

    }
}
