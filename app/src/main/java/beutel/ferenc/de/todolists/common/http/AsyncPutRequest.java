package beutel.ferenc.de.todolists.common.http;

import static java.util.Arrays.asList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import android.os.AsyncTask;
import android.util.Log;
import beutel.ferenc.de.todolists.common.domain.Pair;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class AsyncPutRequest extends AsyncTask<Pair<URL, String>, String, List<AsyncResponse>> {

  private final Consumer<List<AsyncResponse>> onResponse;

  @Override
  @SafeVarargs
  protected final List<AsyncResponse> doInBackground(final Pair<URL, String>... urlPutPairs) {
    final List<AsyncResponse> responses = new ArrayList<>();
    asList(urlPutPairs).forEach(urlPutEntry -> {
      try {
        final byte[] bytesToWrite = urlPutEntry.getRight().getBytes();
        final HttpURLConnection httpURLConnection = (HttpURLConnection) urlPutEntry.getLeft().openConnection();
        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(bytesToWrite.length));
        httpURLConnection.getOutputStream().write(bytesToWrite);
        responses.add(AsyncResponse.builder().responseCode(httpURLConnection.getResponseCode()).build());
      } catch (final Exception exception) {
        Log.d("AsnycRequest", "Error during async request", exception);
      }
    });

    return responses;
  }

  @Override
  protected void onPostExecute(final List<AsyncResponse> result) {
    super.onPostExecute(result);
    onResponse.accept(result);
  }
}
