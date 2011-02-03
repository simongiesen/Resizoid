package de.xgerdax.resizoid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Displays the Preferences Activity.
 * @author xge
 *
 */

public class ResizoidSettingsFrame extends PreferenceActivity {

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
