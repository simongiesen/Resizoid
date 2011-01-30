/*
 * TODO create Function resizeImage() to be used whenever an Image has to be resized. 
 * For Example: saving resized Image, displaying ImageButton etc.
 */

package de.xgerdax.resizoid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

/**
 * 
 * @author xge
 * 
 */

public class ResizoidFrame extends Activity {

	/**
	 * 
	 */
	static final int SELECT_IMAGE = 0;

	/**
	 * Identifier for the About Menu Button.
	 */
	public static final int ABOUT_ID = Menu.FIRST;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            Default.
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
	 *            View we come from
	 */
	public final void selectPictureFromGallery(final View view) {
		Log.i("Resizoid", "Button pressed");
		startActivityForResult(new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
				SELECT_IMAGE);
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
	public final void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				// filePath now Contains the Path to the selected Image
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				// Get the ImageButton
				final ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);

				// Now set the Image
				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

				// Create the Resize Matrix
				int width = yourSelectedImage.getWidth();
				int height = yourSelectedImage.getHeight();
				int newWidth = imageButton1.getWidth();
				int newHeight = imageButton1.getHeight();

				// calculate the scale
				float factor = ((float) newWidth) / width;

				// create matrix for the manipulation
				Matrix matrix = new Matrix();
				// resize the bit map
				matrix.postScale(factor, factor);

				// recreate the new Bitmap
				Bitmap resizedBitmap = Bitmap.createBitmap(yourSelectedImage,
						0, 0, width, height, matrix, true);

				// make a Drawable from Bitmap to allow to set the BitMap
				// to the ImageView, ImageButton or what ever
				BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
				imageButton1.setImageDrawable(bmd);

				// center the Image
				imageButton1.setScaleType(ScaleType.CENTER);

				Log.i("Resizoid",
						"selected Image URI: " + selectedImage.toString());
			}
		}
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
		// About Item with Icon
		menu.add(0, ABOUT_ID, Menu.NONE, "Über").setIcon(
				android.R.drawable.ic_menu_info_details);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Resizes the Picture according to the selected Output Size, then saves it.
	 * 
	 * @param view
	 *            Default.
	 */
	public final void resize(final View view) {

		Context context = getApplicationContext();
		CharSequence text = "Hello toast!";
		int duration = Toast.LENGTH_SHORT;

		Log.i("Resizoid", "Now I call the Toast!");
		Toast.makeText(context, text, duration).show();
		Log.i("Resizoid", "Now it's done!");
	}
}