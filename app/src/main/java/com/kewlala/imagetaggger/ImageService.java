package com.kewlala.imagetaggger;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.kewlala.imagetaggger.data.ImageEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by jhancock2010 on 3/25/18.
 */

class ImageService extends AsyncTaskLoader<ImageEntity> {

    public static final String LOG_TAG = ImageService.class.getSimpleName();
    private Uri mImageUri;
    private Context mAppContext;
    //keys for JSON objects, declared as constants
    //to avoid typos if reused
    private static final String outputs_key = "outputs";
    private static final String concepts_key = "concepts";
    private static final String name_key = "name";
    private static final String probability_key = "value";
    private static final String data_key = "data";
    public final static String BASE_URL = "https://api.clarifai.com";
    //we use general model API
    //see https://clarifai.com/developer/guide/predict#predict for some details
    public static final String PATH = "/v2/models/aaa03c23b3724a16a56b629203edc62c/outputs";

    private static String mApiKey;

    /**
     * constructor
     *
     * @param applicationContext - context of calling activity
     * @param imageUri           - location of image to convert to bytes and send to image classification
     *                           REST service
     */
    public ImageService(Context applicationContext, Uri imageUri) {
        super(applicationContext);
        this.mAppContext = applicationContext;
        this.mImageUri = imageUri;
        if (mApiKey == null){
            AssetsPropertyReader assetsPropertyReader
                    = new AssetsPropertyReader(mAppContext);
            Properties p = assetsPropertyReader.getProperties("apikey.properties");
            mApiKey = p.getProperty("apikey");
            Log.d(LOG_TAG, "ImageSelectActivity read api key " + mApiKey);
        }
    }


    @Override
    public ImageEntity loadInBackground() {
        byte[] imageBytes = imageUriToByteArray();
        classifyPhoto(mApiKey, imageBytes);
        return new ImageEntity(mImageUri.getPath(), getSha256(imageBytes), new Date(System.currentTimeMillis()));
    }

    private String getSha256(byte[] imageBytes) {
        Log.d(LOG_TAG, "getSha256::start");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(imageBytes);
            return new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            Log.d(LOG_TAG, "SHA not found, returning null.");
        }
        return null;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private byte[] imageUriToByteArray() {

        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // open input stream referenced in intent data

        try {
            InputStream inStream = mAppContext.getContentResolver().openInputStream(mImageUri);

            // we need to know how may bytes were read to write them to the byteBuffer
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "did not fid file referenced from uri " + mImageUri.toString(), e);
        } catch (IOException e) {
            Log.d(LOG_TAG, "i/o exception reading file referenced from uri " +
                    mImageUri.toString(), e);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();

    }

    private long writePostData(HttpURLConnection urlConnection, byte[] imageBytes) {
        long dataLength = 0;
        try {
            JSONObject base64 = new JSONObject();
            base64.put("base64", Base64.encodeToString(imageBytes, Base64.DEFAULT));

            JSONObject image = new JSONObject();
            image.put("image", base64);

            JSONObject data = new JSONObject();
            data.put("data", image);

            JSONArray inputs = new JSONArray();
            inputs.put(data);

            JSONObject postJson = new JSONObject();
            postJson.put("inputs", inputs);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            byte[] postJsonBytes = postJson.toString().getBytes();

            out.write(postJsonBytes);
            out.flush();

        } catch (IOException e) {
            Log.d(LOG_TAG, "io exeption attempting to get output stream for post data", e);

        } catch (JSONException e) {
            Log.d(LOG_TAG, "error constructin JSON object ", e);
        } finally {
            return dataLength;
        }
    }

    private ArrayList<PhotoTaggerListItem> classifyPhoto(String apiKey, byte[] imageBytes) {

        Log.d(LOG_TAG, "classify photo:: starting");

        // Create an empty ArrayList that we can start Clarify's model output to
        ArrayList<PhotoTaggerListItem> conceptList = new ArrayList<PhotoTaggerListItem>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            URL url = createUrl(BASE_URL + PATH);
            Log.d(LOG_TAG, "created url" + url.toString());

            JSONObject clarifaiResposne = new JSONObject(makeHttpRequest(url, apiKey, imageBytes));
            Log.d(LOG_TAG, "response " + clarifaiResposne.toString());

            JSONArray outputs = clarifaiResposne.getJSONArray(outputs_key);
            Log.d(LOG_TAG, "outputs: " + outputs.toString());

            int len_outputs = outputs.length();

            for (int i = 0; i < len_outputs; i++) {

                JSONObject dataObj = outputs.getJSONObject(i).getJSONObject(data_key);
                Log.d(LOG_TAG, "dataObj: " + dataObj.toString());

                JSONArray concepts = dataObj.getJSONArray(concepts_key);
                Log.d(LOG_TAG, "concepts: " + concepts.toString());

                int len_concepts = concepts.length();
                Log.d(LOG_TAG, "concepts length:" + len_concepts);

                for (int j = 0; j < len_concepts; j++) {

                    JSONObject curConcept = concepts.getJSONObject(j);
                    Log.d(LOG_TAG, "curConcept: " + concepts.toString());

                    String curName = curConcept.getString(name_key);
                    Log.d(LOG_TAG, "curName: " + curName);


                    double curProbability = Double.NaN;
                    PhotoTaggerListItem listItem;
                    try {
                        curProbability = curConcept.getDouble(probability_key);

                    } catch (JSONException e) {
                        Log.d(LOG_TAG,
                                "unable to parse curProbability = " + curProbability, e);
                    } finally {
                        listItem = new PhotoTaggerListItem(curName, curProbability);
                    }
                    Log.d(LOG_TAG, "list item = " + listItem.toString());
                    conceptList.add(listItem);
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results",
                    e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "i/o exception making http request", e);
        }
        // Return the list of earthquakes
        return conceptList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private  URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private  String makeHttpRequest(URL url, String apiKey, byte[] imageBytes)
            throws IOException {

        Log.d(LOG_TAG, "makeHttpRequest: starting");

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", "Key " + apiKey);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            //setRequestMethod("POST") and setDoOutput apparently do the same thing
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            urlConnection.connect();

            writePostData(urlConnection, imageBytes);

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: "
                        + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake " +
                    "JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

}
