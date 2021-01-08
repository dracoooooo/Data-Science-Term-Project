package jp.mitukiii.tumblife;

import java.io.File;
import jp.mitukiii.tumblife.R;
import jp.mitukiii.tumblife.model.TLSetting.DASHBOARD_TYPE;
import jp.mitukiii.tumblife.util.TLExplorer;
import jp.mitukiii.tumblife.util.TLLog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;

public class Setting extends PreferenceActivity
{ 
  protected Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.layout.setting);

    context = this;

    ListPreference viewMode = (ListPreference) findPreference(getString(R.string.setting_dashboardtype_key));
    viewMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        TLLog.d("onPreferenceChange");
        ListPreference viewMode = (ListPreference) preference;
        viewMode.setValue((String)newValue);
        togglePreference(preference);
        return true;
      }
    });
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    togglePreference(findPreference(getString(R.string.setting_usepin_key)));
    togglePreference(findPreference(getString(R.string.setting_dashboardtype_key)));
  }

  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
  {
    if (getString(R.string.setting_hardkey_key).equals(preference.getKey())) {
      Intent intent = new Intent(context, HardkeySetting.class);
      startActivity(intent);
    } else if (getString(R.string.clearcache_key).equals(preference.getKey())) {
      clearCache();
    } else {
      togglePreference(preference);
    }
    return super.onPreferenceTreeClick(preferenceScreen, preference);
  }

  protected void togglePreference(Preference preference)
  {
    String key = preference.getKey();
    if (getString(R.string.setting_usepin_key).equals(key)) {
      CheckBoxPreference usePin = (CheckBoxPreference) preference;
      ListPreference pinAction = (ListPreference) findPreference(getString(R.string.setting_pinaction_key));
      if (usePin.isChecked()) {
        pinAction.setEnabled(true);
      } else {
        pinAction.setEnabled(false);
      }
    } else if (getString(R.string.setting_dashboardtype_key).equals(key)) {
      ListPreference viewMode = (ListPreference) preference;
      CheckBoxPreference skipPhotos = (CheckBoxPreference) findPreference(getString(R.string.setting_skipphotos_key));
      if (DASHBOARD_TYPE.Default == DASHBOARD_TYPE.valueOf(viewMode.getValue())) {
        skipPhotos.setEnabled(true);
      } else {
        skipPhotos.setEnabled(false);
      }
    }
  }

  protected void clearCache()
  {
    App.isClearCached = true;

    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setTitle(R.string.clearcache_now_title);
    progressDialog.setCancelable(false);
    progressDialog.show();

    final Handler handler = new Handler();

    new Thread() {
      public void run() {
        TLExplorer.deleteFiles(new File(TLExplorer.HTML_DIR).listFiles());
        TLExplorer.deleteFiles(new File(TLExplorer.IMAGE_DIR).listFiles());

        handler.post(new Runnable() {
          public void run() {
            progressDialog.dismiss();
          }
        });
      }
    }.start();
  }
}
