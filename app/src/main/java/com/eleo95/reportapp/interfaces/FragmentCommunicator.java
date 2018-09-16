package com.eleo95.reportapp.interfaces;

import android.net.Uri;

public interface FragmentCommunicator {
    void uploadFile(Uri imgUrl, final String title, String description, String location);
}
