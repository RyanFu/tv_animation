/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jumplife.tvanimation;

import com.google.android.gcm.GCMRegistrar;
import com.jumplife.tvanimation.api.TvAnimationAPI;

import android.content.Context;
import android.util.Log;

import java.util.Random;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String TAG = "GCMUsaDrama";

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register(final Context context, final String regId) {
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            GCMRegistrar.setRegisteredOnServer(context, true);
            
            String regIdShIO = TvAnimationApplication.shIO.getString("reg_id", "");  
            io.vov.utils.Log.d(TAG, "regId : " + regId + " , shio regId : " + regIdShIO);
            if(!regId.equals(regIdShIO)) {            	
	            TvAnimationAPI dramaAPI = new TvAnimationAPI();
				if(dramaAPI.postGcm(regId, context)) {
					TvAnimationApplication.shIO.edit().putString("reg_id", regId).commit();
					return true;
				} else {
					try {
	                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
	                    Thread.sleep(backoff);
	                } catch (InterruptedException e1) {
	                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
	                    Thread.currentThread().interrupt();
	                    return false;
	                }
				}
            }
        }
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId) {
        GCMRegistrar.setRegisteredOnServer(context, false);
    }
}
