package com.ehmer.usa.bill;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

public class Bill {

    public static final int PASSED = 0;
    public static final int PROPOSED = 1;
    public static final int VETOED = 2;
    public static final int FAILED = 3;

    @SerializedName("Name")
    @NonNull
    public String name;

    @SerializedName("State")
    public Integer state;

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @IntDef({
            PASSED,
            PROPOSED,
            VETOED,
            FAILED

    })
    @Retention(RetentionPolicy.SOURCE)
    @interface BillState {
    }

    public Bill() {
        name = UUID.randomUUID().toString();
        state = PROPOSED;
    }

    public Bill(Bill old, @BillState int newState) {
        this.name = old.name;
        this.state = newState;
    }

    public void setState(@BillState int state) {
        this.state = state;
    }

    private String stateAsString() {
        if (state == PASSED) return "Passed";
        if (state == PROPOSED) return "Proposed";
        if (state == VETOED) return "Vetoed";
        if (state == FAILED) return "Failed";
        throw new IllegalStateException("unkonwn state " + state);
    }

    @Override
    public String toString() {
        return String.format("Bill %s [%s]", name.substring(0, 4), stateAsString());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bill bill = (Bill) o;

        return name.equals(bill.name);

    }

}
