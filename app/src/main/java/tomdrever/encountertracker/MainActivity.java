package tomdrever.encountertracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import tomdrever.encountertracker.data.BaseInitiativeEntry;
import tomdrever.encountertracker.dialogs.NpcDialogFragment;
import tomdrever.encountertracker.dialogs.OtherEntryDialogFragment;
import tomdrever.encountertracker.dialogs.PcDialogFragment;
import tomdrever.encountertracker.list.EmptyViewRecyclerView;
import tomdrever.encountertracker.list.InitiativeListAdapter;
import tomdrever.encountertracker.list.InitiativeListUpdatedListener;

public class MainActivity extends AppCompatActivity implements FabSpeedDial.OnMenuItemClickListener,
        InitiativeListUpdatedListener {

    @BindView(R.id.fab)
    FabSpeedDial fab;

    @BindView(R.id.initiative_list_recycler_view)
    EmptyViewRecyclerView initiativeListRecyclerView;

    InitiativeListAdapter initiativeListAdapter;
    private LinearLayoutManager layoutManager;

    ArrayList<BaseInitiativeEntry> initiativeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.addOnMenuItemClickListener(this);

        if (savedInstanceState != null)
            initiativeList = savedInstanceState.getParcelableArrayList("initiative");
        else
            initiativeList = new ArrayList<>();

        initiativeListRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        initiativeListRecyclerView.setLayoutManager(layoutManager);

        initiativeListAdapter = new InitiativeListAdapter(initiativeList, this);
        initiativeListRecyclerView.setAdapter(initiativeListAdapter);

        initiativeListRecyclerView.setEmptyView(findViewById(R.id.no_entries_text_view));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("initiative", initiativeList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        initiativeList = savedInstanceState.getParcelableArrayList("initiative");
        initiativeListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(this);
                confirmDialogBuilder.setTitle("Confirm");
                confirmDialogBuilder.setMessage("Are you sure you want to clear the current initiative list?");

                confirmDialogBuilder.setPositiveButton("Clear",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                initiativeList.clear();
                                initiativeListAdapter.notifyDataSetChanged();
                            }
                        });

                confirmDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                confirmDialogBuilder.create().show();

                return true;

            case R.id.action_settings:
                AlertDialog.Builder settingsDialogBuilder = new AlertDialog.Builder(this);
                settingsDialogBuilder.setTitle("Settings");
                settingsDialogBuilder.setMessage("PC names for autocomplete:");

                // Get names from preferences, use an empty list if none exist
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                HashSet<String> names = (HashSet<String>) sharedPref.getStringSet(getString(R.string.names_pref), new HashSet<String>());

                final EditText input = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                input.setText(joinStringArray(names.toArray(new String[]{})));
                input.setPadding(16, 0, 16, 0);

                settingsDialogBuilder.setView(input);

                settingsDialogBuilder.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String s = input.getText().toString();

                                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putStringSet("names", new HashSet<>(Arrays.asList(s.replace(" ", "").split(","))));
                                editor.apply();
                            }
                        });

                settingsDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                settingsDialogBuilder.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String joinStringArray(String[] array) {
        if (array.length > 0) {
            StringBuilder builder = new StringBuilder();

            for (String n : array) {
                builder.append(n).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());

            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public void onMenuItemClick(FloatingActionButton miniFab, @Nullable TextView label, int itemId) {
        switch (itemId) {
            case R.id.fab_menu_new_entry:
                openFragment(OtherEntryDialogFragment.newInstance(this));
                break;
            case R.id.fab_menu_new_pc:
                openFragment(PcDialogFragment.newInstance(this));
                break;
            case R.id.fab_menu_new_npc:
                openFragment(NpcDialogFragment.newInstance(this));
                break;
            default:
                Log.d("Main", "onMenuItemClick: Wut");
                break;

        }
    }

    public void openFragment(Fragment fragment) {
        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
        transaction.replace(android.R.id.content, fragment, "TAG")
                .commit();
    }

    @Override
    public void onNewEntryAdded(BaseInitiativeEntry entry) {
        initiativeList.add(entry);
        Collections.sort(initiativeList);
        initiativeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEntryUpdated(BaseInitiativeEntry entry, int position) {
        initiativeList.set(position, entry);
        Collections.sort(initiativeList);
        initiativeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("TAG");

        if (fragment != null)
            fragment.dismiss();
        else
            super.onBackPressed();
    }
}
