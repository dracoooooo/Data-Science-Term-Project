/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

package com.commonsware.android.geoweb2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.webkit.WebView;
import org.json.JSONException;
import org.json.JSONObject;

public class GeoWebTwo extends Activity {
  private static String PROVIDER="gps";
  private WebView browser;
  private LocationManager myLocationManager=null;
  
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    browser=(WebView)findViewById(R.id.webkit);
    
    myLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
    
    browser.getSettings().setJavaScriptEnabled(true);
    browser.addJavascriptInterface(new Locater(), "locater");
    browser.loadUrl("file:///android_asset/geoweb2.html");
  }
  
  @Override
  public void onResume() {
    super.onResume();
    myLocationManager.requestLocationUpdates(PROVIDER, 0,
                                              0,
                                              onLocation);
  }
  
  @Override
  public void onPause() {
    super.onPause();
    myLocationManager.removeUpdates(onLocation);
  }
  
  LocationListener onLocation=new LocationListener() {
    public void onLocationChanged(Location location) {
      StringBuilder buf=new StringBuilder("javascript:whereami(");
      
      buf.append(String.valueOf(location.getLatitude()));
      buf.append(",");
      buf.append(String.valueOf(location.getLongitude()));
      buf.append(")");
      
      browser.loadUrl(buf.toString());
    }
    
    public void onProviderDisabled(String provider) {
      // required for interface, not used
    }
    
    public void onProviderEnabled(String provider) {
      // required for interface, not used
    }
    
    public void onStatusChanged(String provider, int status,
                                  Bundle extras) {
      // required for interface, not used
    }
  };
  
  public class Locater {
    public String getLocation() throws JSONException {
      Location loc=myLocationManager.getLastKnownLocation(PROVIDER);
      
      if (loc==null) {
        return(null);
      }
      
      JSONObject json=new JSONObject();

      json.put("lat", loc.getLatitude());
      json.put("lon", loc.getLongitude());
      
      return(json.toString());
    }
  }
}