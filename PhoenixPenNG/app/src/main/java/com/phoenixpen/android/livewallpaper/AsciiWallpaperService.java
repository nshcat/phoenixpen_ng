package com.phoenixpen.android.livewallpaper;

import android.content.SharedPreferences;
import android.opengl.GLSurfaceView.Renderer;
import android.preference.PreferenceManager;

import com.phoenixpen.android.application.AsciiApplication;


public class AsciiWallpaperService extends OpenGLES31WallpaperService {
	@Override
	Renderer getNewRenderer()
	{
		registerSettingsListener();

		return new AsciiApplication(this);
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