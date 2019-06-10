package com.phoenixpen.android.livewallpaper;

import android.content.SharedPreferences;
import android.opengl.GLSurfaceView.Renderer;
import android.preference.PreferenceManager;

import com.phoenixpen.android.game.core.AsciiApplication;
import com.phoenixpen.android.input.NullInputProvider;


public class AsciiWallpaperService extends OpenGLES31WallpaperService {
	@Override
	Renderer getNewRenderer()
	{
		registerSettingsListener();

		return new AsciiApplication(this, new NullInputProvider());
	}

	private void registerSettingsListener()
	{
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
				Configuration.Companion.getInstance().update(sharedPreferences);
			}
		};

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(listener);
	}

	private SharedPreferences.OnSharedPreferenceChangeListener listener;
}
