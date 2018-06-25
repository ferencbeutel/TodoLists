package beutel.ferenc.de.todolists.contact.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.contact.domain.Contact;
import beutel.ferenc.de.todolists.todo.domain.Todo;

import java.util.List;

public class ContactArrayAdapter extends ArrayAdapter<Contact> {
    private Context context;
    private List<Contact> contacts;

    public ContactArrayAdapter(final Context context, final List<Contact> contacts) {
        super(context, 0, contacts);
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        final View listItem = convertView == null ?
                LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false) :
                convertView;

        final Contact currentContact = contacts.get(position);

        final TextView contactNameView = listItem.findViewById(R.id.contact_name);
        contactNameView.setText(currentContact.getName());

        return listItem;

    }
}
