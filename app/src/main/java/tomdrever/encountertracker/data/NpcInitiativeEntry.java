package tomdrever.encountertracker.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;

import java.util.ArrayList;
import java.util.Arrays;

import tomdrever.encountertracker.R;

public class NpcInitiativeEntry extends BaseInitiativeEntry {
    private ArrayList<HpItem> hpItems;

    public NpcInitiativeEntry(String name, int initiative, ArrayList<HpItem> items) {
        super(name, initiative);

        this.hpItems = items;
    }

    public int getNumber() {
        return hpItems.size();
    }

    public ArrayList<HpItem> getHpItems() {
        return hpItems;
    }

    @Override
    public @ColorRes int getColour() {
        return R.color.colorNpc;
    }

    public static class HpItem implements Parcelable{

        private int currentHp;
        private int maxHp;

        public HpItem(int hp) {
            this.currentHp = hp;
            this.maxHp = hp;
        }

        public int getCurrentHp() {
            return currentHp;
        }

        public void setCurrentHp(int currentHp) {
            this.currentHp = currentHp;
        }

        public int getMaxHp() {
            return maxHp;
        }

        public void setMaxHp(int maxHp) {
            this.maxHp = maxHp;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(currentHp);
            out.writeInt(maxHp);
        }

        /** recreate object from parcel */
        private HpItem(Parcel in) {
            currentHp = in.readInt();
            maxHp = in.readInt();
        }

        public static final Parcelable.Creator<HpItem> CREATOR
                = new Parcelable.Creator<HpItem>() {

            public HpItem createFromParcel(Parcel in) {
                return new HpItem(in);
            }

            public HpItem[] newArray(int size) {
                return new HpItem[size];
            }
        };
    }

    // Parcelable
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);

        out.writeParcelableArray(hpItems.toArray(new HpItem[]{}), flags);
    }

    /** recreate object from parcel */
    private NpcInitiativeEntry(Parcel in) {
        super(in);

        hpItems = new ArrayList<>(Arrays.asList((HpItem[]) in.readParcelableArray(ClassLoader.getSystemClassLoader())));
    }

    public static final Parcelable.Creator<BaseInitiativeEntry> CREATOR
            = new Parcelable.Creator<BaseInitiativeEntry>() {

        public NpcInitiativeEntry createFromParcel(Parcel in) {
            return new NpcInitiativeEntry(in);
        }

        public NpcInitiativeEntry[] newArray(int size) {
            return new NpcInitiativeEntry[size];
        }
    };
}
