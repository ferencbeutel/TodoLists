package beutel.ferenc.de.todolists.contact.domain;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import beutel.ferenc.de.todolists.R;
import lombok.Builder;
import lombok.Data;

import java.io.FileDescriptor;

@Data
@Builder
public class Contact {
    private final String _id;
    private final String name;
    private final Uri contactUri;
    @Nullable
    private final Uri profileImageUri;

    private static final int DEFAULT_PROFILE_IMAGE_ID = R.drawable.ic_default_contact;

    public Bitmap profileImageBitmap(final Context context) {
        try {
            if (profileImageUri == null) {
                return BitmapFactory.decodeResource(context.getResources(), DEFAULT_PROFILE_IMAGE_ID);
            }
            final AssetFileDescriptor assetFileDescriptor = context.getContentResolver()
                    .openAssetFileDescriptor(profileImageUri, "r");
            if (assetFileDescriptor == null) {
                return null;
            }

            final FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
            if (fileDescriptor == null) {
                return null;
            }

            return BitmapFactory.decodeFileDescriptor(fileDescriptor);
        } catch (Exception exception) {
            Log.d("ContactImageBitmap", "Error during contact image bitmap generation", exception);
        }

        return null;
    }
}