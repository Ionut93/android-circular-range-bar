package com.circularrangebar.CircularRangeBar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by irina on 2/1/2017.
 */

public class AppointmentViewModel implements Parcelable {

    protected int startHour;
    protected int endHour;
    protected int startMinute;
    protected int endMinute;

    public AppointmentViewModel(int endHour, int endMinute, int startHour, int startMinute) {
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.startHour = startHour;
        this.startMinute = startMinute;
    }

    protected AppointmentViewModel(Parcel in) {
        startHour = in.readInt();
        endHour = in.readInt();
        startMinute = in.readInt();
        endMinute = in.readInt();
    }

    public static final Creator<AppointmentViewModel> CREATOR = new Creator<AppointmentViewModel>() {
        @Override
        public AppointmentViewModel createFromParcel(Parcel in) {
            return new AppointmentViewModel(in);
        }

        @Override
        public AppointmentViewModel[] newArray(int size) {
            return new AppointmentViewModel[size];
        }
    };

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startHour);
        dest.writeInt(endHour);
        dest.writeInt(startMinute);
        dest.writeInt(endMinute);
    }
}
