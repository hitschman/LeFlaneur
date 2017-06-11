package ch.cf.leflaneur.Services;

import android.location.Location;

/**
 * Created by christianfallegger on 06.04.17.
 */

public interface ServiceCallbacks {
    void onServiceLocationChanged(Location location);
}
