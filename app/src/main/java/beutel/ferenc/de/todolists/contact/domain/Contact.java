package beutel.ferenc.de.todolists.contact.domain;

import android.net.Uri;
import android.support.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Contact {
    private final int _id;
    private final String name;
    private final @Nullable Uri profileImageUri;
}
