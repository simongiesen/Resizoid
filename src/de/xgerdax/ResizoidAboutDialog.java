package de.xgerdax;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Displays the About Dialog.
 *
 * @author xge
 */
public final class ResizoidAboutDialog {

    /**
     * Padding Value.
     */
    static final int PADDING = 5;

    /**
     * Private Constructor to prevent instantiation.
     */
    private ResizoidAboutDialog() {
    }

    /**
     * Creates the About Dialog.
     *
     * @param context Default param.
     * @return The Dialog itself.
     * @throws NameNotFoundException Just in Case...
     */
    public static AlertDialog create(final Context context) throws NameNotFoundException {
        // Try to load the a package matching the name of our own package
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                PackageManager.GET_META_DATA);
        String versionInfo = pInfo.versionName;

        String aboutTitle = String.format("About %s", context.getString(R.string.app_name));
        String versionString = String.format("Version: %s", versionInfo);
        String aboutText = "Autor: xge\n";
        aboutText += "Website: https://github.com/xge/Resizoid";

        // Set up the TextView
        final TextView message = new TextView(context);
        // We'll use a spannablestring to be able to make links clickable
        final SpannableString s = new SpannableString(aboutText);

        // Set some padding
        message.setPadding(PADDING, PADDING, PADDING, PADDING);
        // Set up the final string
        message.setText(versionString + "\n\n" + s);
        // Now linkify the text
        Linkify.addLinks(message, Linkify.ALL);

        return new AlertDialog.Builder(context).setTitle(aboutTitle).setCancelable(true).setIcon(R.drawable.icon)
                .setPositiveButton(context.getString(android.R.string.ok), null).setView(message).create();
    }
}
