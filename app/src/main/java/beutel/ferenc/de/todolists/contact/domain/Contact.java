package beutel.ferenc.de.todolists.contact.domain;

import lombok.Builder;
import lombok.Data;

import android.net.Uri;
import android.support.annotation.Nullable;

@Data
@Builder
public class Contact {
    private final int _id;
    private final String name;
    @Nullable
    private final Uri profileImageUri;
}
