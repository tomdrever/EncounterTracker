package tomdrever.encountertracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tomdrever.encountertracker.data.NpcInitiativeEntry;

public class HpListAdapter extends RecyclerView.Adapter<HpListAdapter.ViewHolder> {

    private ArrayList<NpcInitiativeEntry.HpItem> hpList;
    private Context context;

    public HpListAdapter(Context context, ArrayList<NpcInitiativeEntry.HpItem> hpList) {
        this.context = context;
        this.hpList = hpList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_hp_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        if (hpList.size() > 1) {
            holder.removeHpItem.setVisibility(View.VISIBLE);
            holder.removeHpItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hpList.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.removeHpItem.setVisibility(View.INVISIBLE);
            holder.removeHpItem.setOnClickListener(null);
        }

        holder.editHpValue.setText(hpList.get(i).getMaxHp() != 0 ?
                String.valueOf(hpList.get(i).getMaxHp()) : "");

        holder.editHpValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();

                hpList.get(holder.getAdapterPosition()).setMaxHp(
                        text.equals("") ? 0 : Integer.valueOf(text)
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return hpList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_hp_value)
        EditText editHpValue;

        @BindView(R.id.item_hp_remove)
        ImageButton removeHpItem;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}
