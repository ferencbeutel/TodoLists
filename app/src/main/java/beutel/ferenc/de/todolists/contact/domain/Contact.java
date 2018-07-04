package beutel.ferenc.de.todolists.contact.domain;

import java.io.FileDescriptor;

import lombok.Builder;
import lombok.Data;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import beutel.ferenc.de.todolists.R.drawable;

@Data
@Builder
public class Contact {

  private static final int DEFAULT_PROFILE_IMAGE_ID = drawable.ic_default_contact;
  private final String _id;
  private final String name;
  private final Uri contactUri;
  @Nullable
  private final Uri profileImageUri;

  public Bitmap profileImageBitmap(final Context context) {
    try {
      if (profileImageUri == null) {
        return BitmapFactory.decodeResource(context.getResources(), DEFAULT_PROFILE_IMAGE_ID);
      }
      final AssetFileDescriptor assetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(profileImageUri, "r");
      if (assetFileDescriptor == null) {
        return null;
      }

      final FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
      if (fileDescriptor == null) {
        return null;
      }

      return BitmapFactory.decodeFileDescriptor(fileDescriptor);
    } catch (final Exception exception) {
      Log.d("ContactImageBitmap", "Error during contact image bitmap generation", exception);
    }

    return null;
  }
}