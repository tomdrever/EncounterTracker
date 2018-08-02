package tomdrever.encountertracker.data;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

public abstract class BaseInitiativeEntry implements Comparable<BaseInitiativeEntry>, Parcelable {
    private String name;
    private int initiative;

    public BaseInitiativeEntry(String name, int initiative) {
        this.name = name;
        this.initiative = initiative;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract @ColorRes int getColour();

    // implements Comparable
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(@NonNull BaseInitiativeEntry otherEntry) {
        return Integer.compare(otherEntry.getInitiative(), initiative);
    }

    // implements Parcelable
    public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(initiative);
    }

    /** recreate object from parcel */
    protected BaseInitiativeEntry(Parcel in) {
        name = in.readString();
        initiative = in.readInt();
    }
}
