/*
 * TODO: Check Workflow: Select Picture -> Select Size -> resize -> save
 */
package de.xgerdax;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Activity.
 * 
 * @author xge
 * 
 */
public class ResizoidActivity extends Activity {

    /**
     * Constant used for Intent-switching.
     */
    private static final int SELECT_IMAGE = 0;

    /**
     * Identifier for the Settings Menu Button.
     */
    private static final int SETTINGS_ID = Menu.FIRST;

    /**
     * Identifier for the About Menu Button.
     */
    private static final int ABOUT_ID = Menu.NONE;

    /**
     * After picking the Image in the Gallery its stored in this Variable.
     */
    private static Bitmap selectedImage;

    /**
     * Degree of Compression.
     */
    private static final int JPEG_COMPRESSION = 85;

    /**
     * If all fails the scaled Image will be this wide.
     */
    private static final int DEFAULT_WIDTH = 600;

    /**
     * Number of chars before the original filename gets cut.
     */
    private static final int FILENAME_LENGTH = 12;

    /**
     * File Name of the selected image.
     */
    private static String selectedFileName;

    private static final String TAG = "Resizoid";

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            only for Compliance Needs
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /**
     * Pick Image Button is clicked.
     * 
     * @param view
     *            The View we come from
     */
    public final void selectPictureFromGallery(final View view) {
        Log.i(TAG, "Button pressed");
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), SELECT_IMAGE);
    }

    /**
     * Image is picked, let's work with it!
     * 
     * @param requestCode
     *            Constant Requestcode
     * @param resultCode
     *            Constant Resultcode
     * @param data
     *            Intent we come from
     */
    @Override
    public final void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                // Get the Image and store it in selectedImage
                String[] filePathColumn = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                cursor.moveToFirst();

                // Get the File's Path
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);

                // Get the File's Size
                columnIndex = cursor.getColumnIndex(filePathColumn[1]);
                String fileSize = cursor.getString(columnIndex);
                // and display it in the corresponding TextView
                // (details_filesize)
                TextView details_filesize = (TextView) findViewById(R.id.details_filesize);
                int fileSizeKb = Integer.parseInt(fileSize) / 1024;
                details_filesize.setText(fileSizeKb + " kb");

                // Get the File's Name
                columnIndex = cursor.getColumnIndex(filePathColumn[2]);
                selectedFileName = cursor.getString(columnIndex).substring(0, FILENAME_LENGTH);
                selectedFileName.replaceAll(" ", "_");
                // and display it in the corresponding TextView
                // (details_filename)
                TextView details_filename = (TextView) findViewById(R.id.details_filename);
                details_filename.setText(cursor.getString(columnIndex));

                // Do some logging and finally close the Cursor
                Log.i(TAG, "filePath: " + filePath + " fileSize: " + fileSize + " fileName: " + selectedFileName);
                cursor.close();

                // Now set the Image
                selectedImage = BitmapFactory.decodeFile(filePath);

                // Display the Picture's Measurements in the corresponding
                // TextView
                TextView details_picsize = (TextView) findViewById(R.id.details_picsize);
                details_picsize.setText(selectedImage.getWidth() + "x" + selectedImage.getHeight() + "px");

                // Display the Picture itself. Scaled!
                ImageView img_preview = (ImageView) findViewById(R.id.img_preview);
                img_preview.setImageBitmap(resizeSelectedImage(img_preview.getWidth()));

                // Finally enable the main button
                Button btn_main = (Button) findViewById(R.id.btn_main);
                btn_main.setEnabled(true);

            }
        }
    }

    /**
     * Resize the picked Image and return it.
     * 
     * @param desiredWidth
     *            An integer Value of the Width you want the Image scaled to.
     * @return The resized Image.
     */
    public final Bitmap resizeSelectedImage(final int desiredWidth) {

        // Now set the Image
        Bitmap yourSelectedImage = selectedImage;

        // Create the Resize Matrix
        int width = yourSelectedImage.getWidth();
        int height = yourSelectedImage.getHeight();

        int newWidth = desiredWidth;

        // calculate the scale
        float factor = ((float) newWidth) / width;

        // create matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(factor, factor);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(yourSelectedImage, 0, 0, width, height, matrix, true);

        // make a Drawable from Bitmap to allow to set the BitMap
        // to the ImageView, ImageButton or what ever
        return resizedBitmap;
    }

    /**
     * Options Menu with Buttons. 1st Button: Settings 2nd Button: About
     * 
     * @param menu
     *            Instance of the Menu
     * 
     * @return Everything okay?
     */
    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        // Settings Item with Icon
        menu.add(0, SETTINGS_ID, Menu.NONE, "Einstellungen").setIcon(android.R.drawable.ic_menu_preferences);
        // About Item with Icon
        menu.add(0, ABOUT_ID, Menu.NONE, "†ber").setIcon(android.R.drawable.ic_menu_info_details);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Event triggered after selecting an entry of the menu.
     * 
     * @param item
     *            the selected entry
     * @return returns default values.
     */
    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {

        case SETTINGS_ID:

            Intent i = new Intent(this, ResizoidSettingsFrame.class);
            startActivity(i);

            break;

        case ABOUT_ID:

            AlertDialog builder;
            try {
                builder = ResizoidAboutDialog.create(this);
                builder.show();
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            break;

        default:

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Writes the given image to the SD-Card.
     * 
     * @param img
     *            the image you want to save.
     * @throws IOException
     *             If there's something wrong with the OutputStream.
     */
    public final void saveToDisk(final Bitmap img) throws IOException {
        // get the user defined path from the settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // create the actual file path (with a fallback)
        String path = Environment.getExternalStorageDirectory().toString();

        String userDefinedPath = prefs.getString("speicherortPref", "/Resizoid");

        // Make sure there's a slash between the directories
        path += (!userDefinedPath.substring(0, 1).equals("/")) ? "/" : "";

        path += userDefinedPath;

        Log.i(TAG, "path: " + path);

        // finally create the file
        File file = new File(path, selectedFileName + "-" + img.getWidth() + ".jpg");

        try {
            // test whether its possible to write at the exact location
            file.mkdirs();
        } catch (SecurityException e) {
            Log.i(TAG, "Anlegen des Verzeichnisses gescheitert!, e: " + e.getMessage());
        }

        Log.i(TAG, "Anlegen des Verzeichnisses hat geklappt!");

        // do the actual writing
        FileOutputStream fOut = new FileOutputStream(file);
        img.compress(Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION, fOut);
        fOut.flush();
        fOut.close();
        Log.i(TAG, "FileOutput is done and closed!");

        // put the new image in the gallery
        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),
                file.getName());
        Log.i(TAG, "MediaStore is done!");
    }

    /**
     * Resizes the Picture according to the selected Output Size, then saves it.
     * 
     * @param view
     *            Default.
     */
    public final void resize(final View view) {

        Spinner s = (Spinner) findViewById(R.id.spin_sizes);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Based on the selected size we have to get the preference's value
        int size;
        switch (s.getSelectedItemPosition()) {
        case 0:
            size = Integer.parseInt(prefs.getString("sizeSmallPref", "600"));
            break;
        case 1:
            size = Integer.parseInt(prefs.getString("sizeMediumPref", "1024"));
            break;
        case 2:
            size = Integer.parseInt(prefs.getString("sizeLargePref", "1920"));
            break;
        default:
            size = DEFAULT_WIDTH;
        }

        Log.i(TAG, "size: " + size);
        Bitmap resizedImg = resizeSelectedImage(size);
        Log.i(TAG, "Bitmap Hšhe: " + resizedImg.getHeight() + " Bitmap Breite: " + resizedImg.getWidth() + " Item: "
                + s.getSelectedItem().toString());

        try {
            saveToDisk(resizedImg);
            Context context = getApplicationContext();
            CharSequence text = "Bild erfolgreich verkleinert!";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, text, duration).show();

        } catch (IOException e) {
            Log.i(TAG, "Could not write File " + e.getMessage());
        }
    }

}
