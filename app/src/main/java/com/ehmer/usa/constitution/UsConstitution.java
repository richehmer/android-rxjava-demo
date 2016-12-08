package com.ehmer.usa.constitution;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class UsConstitution implements Constitution {

    @NonNull
    @SerializedName("Ratified")
    public Boolean ratified;

    public UsConstitution(boolean ratified) {
        this.ratified = ratified;
    }

    @Override
    public boolean isRatified() {
        return ratified;
    }
}
