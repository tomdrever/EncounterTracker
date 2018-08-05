package tomdrever.encountertracker.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import tomdrever.encountertracker.OnDeleteClickedListener;
import tomdrever.encountertracker.R;
import tomdrever.encountertracker.data.BaseInitiativeEntry;
import tomdrever.encountertracker.list.InitiativeListUpdatedListener;

public abstract class BaseDialogFragment extends DialogFragment {

    InitiativeListUpdatedListener initiativeListUpdatedListener;

    OnDeleteClickedListener onDeleteClickedListener;

    protected abstract String getTitle();

    protected abstract boolean saveEntry();

    int position = -1;
    BaseInitiativeEntry entry;

    @BindView(R.id.entry_name_edit_text)
    EditText entryName;

    @BindView(R.id.entry_intiative_edit_text)
    EditText entryInitiative;

    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_entry, container, false);
        ButterKnife.bind(this, view);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white);
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (position != -1) {
            entryName.setText(entry.getName());
            entryInitiative.setText(String.valueOf(entry.getInitiative()));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_fragment, menu);

        if (onDeleteClickedListener != null) {
            menu.findItem(R.id.action_delete).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (saveEntry())
                dismiss();
            else {
                Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                animShake.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));

                getView().startAnimation(animShake);
            }

            return true;
        } else if (id == android.R.id.home) {
            dismiss();
            return true;
        } else if (id == R.id.action_delete) {
            if (onDeleteClickedListener != null) {
                onDeleteClickedListener.onDeleteClicked(position);
            }

            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setOnDeleteClickedListener(OnDeleteClickedListener listener) {
        this.onDeleteClickedListener = listener;
    }


    @Override
    public void dismiss() {
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();

        if (view == null)
            view = new View(getActivity());

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Animate out and remove fragment
        final FragmentActivity activity = getActivity();

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_down);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.remove(BaseDialogFragment.this);
                    ft.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        if (getView() != null) getView().startAnimation(animation);
    }
}
