package tomdrever.encountertracker.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tomdrever.encountertracker.MainActivity;
import tomdrever.encountertracker.R;
import tomdrever.encountertracker.data.BaseInitiativeEntry;
import tomdrever.encountertracker.data.NpcInitiativeEntry;
import tomdrever.encountertracker.data.OtherInitiativeEntry;
import tomdrever.encountertracker.data.PcInitiativeEntry;
import tomdrever.encountertracker.dialogs.NpcDialogFragment;
import tomdrever.encountertracker.dialogs.OtherEntryDialogFragment;
import tomdrever.encountertracker.dialogs.PcDialogFragment;

public class InitiativeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<BaseInitiativeEntry> items;
    private Context context;

    public InitiativeListAdapter(ArrayList<BaseInitiativeEntry> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_initiative_entry, parent, false);

        switch (viewType) {
            case 0:
                return new PcEntryViewHolder(v);

            case 1:
                return new NpcEntryViewHolder(v);

            default:
                return new OtherEntryViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                bindPcEntryViewHolder((PcEntryViewHolder) viewHolder, position);
                break;
            case 1:
                bindNpcEntryViewHolder((NpcEntryViewHolder) viewHolder, position);
                break;
            default:
                bindEntryViewHolder((OtherEntryViewHolder) viewHolder, position);
                break;
        }
    }

    private void bindBaseEntryViewHolder(BaseEntryViewHolder holder, int position) {

        int itemColour = ContextCompat.getColor(context, items.get(position).getColour());

        holder.nameTextView.setText(items.get(position).getName());
        holder.nameTextView.setTextColor(itemColour);

        holder.initiativeTextView.setText(String.valueOf(items.get(position).getInitiative()));
        holder.initiativeTextView.setTextColor(itemColour);
    }

    private void bindEntryViewHolder(OtherEntryViewHolder holder, final int position) {
        bindBaseEntryViewHolder(holder, position);

        holder.listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // New OtherEntryDialogFragment
                MainActivity activity = ((MainActivity) context);
                activity.openFragment(OtherEntryDialogFragment.newInstance(activity, (OtherInitiativeEntry) items.get(position), position));
            }
        };
    }

    private void bindNpcEntryViewHolder(final NpcEntryViewHolder holder, final int position) {
        bindBaseEntryViewHolder(holder, position);

        final NpcInitiativeEntry item = (NpcInitiativeEntry) items.get(position);
        if (item.getNumber() > 1) {
            holder.nameTextView.append(" x" + String.valueOf(((NpcInitiativeEntry) items.get(position)).getNumber()));
        }

        holder.listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // New NpcDialogFragment
                MainActivity activity = ((MainActivity) context);
                activity.openFragment(NpcDialogFragment.newInstance(activity, (NpcInitiativeEntry) items.get(position), position));
            }
        };

        holder.hpDisplayList.removeAllViews();

        for (int i = 0; i < item.getNumber(); i++) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_hp_display, holder.hpDisplayList, false);

            final NpcInitiativeEntry.HpItem hpItemCurr = item.getHpItems().get(i);

            ((TextView) view.findViewById(R.id.hp)).setText(String.format(
                    Locale.ENGLISH, "%d/%d",
                    hpItemCurr.getCurrentHp(),
                    hpItemCurr.getMaxHp()));

            // -- Take away hp --
            view.findViewById(R.id.decrease_hp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reduceHp(hpItemCurr, view);
                }
            });

            // -- Add hp --
            view.findViewById(R.id.increase_hp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    increaseHp(hpItemCurr, view);
                }
            });

            holder.hpDisplayList.addView(view);
        }
    }

    private void bindPcEntryViewHolder(PcEntryViewHolder holder, final int position) {
        bindBaseEntryViewHolder(holder, position);

        holder.listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // New PcDialogFragment
                MainActivity activity = ((MainActivity) context);
                activity.openFragment(PcDialogFragment.newInstance(activity, (PcInitiativeEntry) items.get(position), position));
            }
        };
    }

    private void reduceHp(NpcInitiativeEntry.HpItem hpItem, final View view) {

        getAmountToChangeBy("Reduce HP", hpItem, new OnAmountConfirmedListener() {
            @Override
            public void OnAmountConfirmed(NpcInitiativeEntry.HpItem hpItem, int amount) {
                View layoutView = (View) view.getParent();

                int newHp = hpItem.getCurrentHp() - amount;

                // Cannot go below 0
                hpItem.setCurrentHp(newHp >= 0 ? newHp : 0);

                ((TextView) layoutView.findViewById(R.id.hp)).setText(String.format(
                        Locale.ENGLISH, "%d/%d",
                        hpItem.getCurrentHp(),
                        hpItem.getMaxHp()));
            }
        });
    }

    private void increaseHp(NpcInitiativeEntry.HpItem hpItem, final View view) {
        getAmountToChangeBy("Increase HP", hpItem, new OnAmountConfirmedListener() {
            @Override
            public void OnAmountConfirmed(NpcInitiativeEntry.HpItem hpItem, int amount) {
                View layoutView = (View) view.getParent();

                int newHp = hpItem.getCurrentHp() + amount;

                // Cannot go above max
                hpItem.setCurrentHp(newHp <= hpItem.getMaxHp() ? newHp : hpItem.getMaxHp());

                ((TextView) layoutView.findViewById(R.id.hp)).setText(String.format(
                        Locale.ENGLISH, "%d/%d",
                        hpItem.getCurrentHp(),
                        hpItem.getMaxHp()));
            }
        });
    }

    private void getAmountToChangeBy(String change, final NpcInitiativeEntry.HpItem hpItem,
                                     final OnAmountConfirmedListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Change");
        alertDialog.setMessage("Enter amount:");

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setPadding(16, 0, 16, 0);

        alertDialog.setView(input);

        alertDialog.setPositiveButton(change,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int amount = Integer.valueOf(input.getText().toString());
                        listener.OnAmountConfirmed(hpItem, amount);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    interface OnChangeHpClickedListener {
        void OnHpChangeClicked(NpcInitiativeEntry.HpItem hpItem, View view);
    }

    interface OnAmountConfirmedListener {
        void OnAmountConfirmed(NpcInitiativeEntry.HpItem hpItem, int amount);
    }

    @Override
    public int getItemViewType(int position) {
        BaseInitiativeEntry entry = items.get(position);

        if (entry instanceof PcInitiativeEntry) return 0;

        if (entry instanceof NpcInitiativeEntry) return 1;

        return 2;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BaseEntryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.item_entry_name)
        TextView nameTextView;

        @BindView(R.id.item_entry_initiative)
        TextView initiativeTextView;

        View.OnClickListener listener;

        BaseEntryViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.item_entry_header)
        void onClick(View view) {
            listener.onClick(view);
        }
    }

    static class OtherEntryViewHolder extends BaseEntryViewHolder {
        OtherEntryViewHolder(View v) {
            super(v);
        }
    }

    static class PcEntryViewHolder extends BaseEntryViewHolder {
        PcEntryViewHolder(View v) {
            super(v);
        }
    }

    static class NpcEntryViewHolder extends BaseEntryViewHolder {
        @BindView(R.id.hp_display_list)
        LinearLayout hpDisplayList;

        NpcEntryViewHolder(View v) {
            super(v);
        }
    }
}
