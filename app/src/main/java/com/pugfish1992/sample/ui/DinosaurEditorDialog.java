package com.pugfish1992.sample.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.pugfish1992.sample.R;
import com.pugfish1992.sample.data.Dinosaur;
import com.pugfish1992.sample.data.DinosaurTableContract;
import com.pugfish1992.sample.data.Period;
import com.pugfish1992.sqliteflow.core.AbsValidator;

import java.util.Set;

/**
 * Created by daichi on 11/23/17.
 */

public class DinosaurEditorDialog extends DialogFragment {

    public interface OnSaveSuccessfulListener {
        void onSaveSuccessful(@NonNull Dinosaur dinosaur, boolean isNew);
    }

    private static final String ARG_DINOSAUR_ID = "DinosaurEditorDialog:id";
    private static final String ARG_DINOSAUR_NAME = "DinosaurEditorDialog:name";

    private OnSaveSuccessfulListener mListener;
    private Dinosaur mDinosaur = null;
    private boolean mIsNew = false;

    public static DinosaurEditorDialog newInstance(Dinosaur dinosaur) {
        DinosaurEditorDialog fragment = new DinosaurEditorDialog();
        if (dinosaur != null) {
            Bundle args = new Bundle();
            args.putLong(ARG_DINOSAUR_ID, dinosaur.getId());
            args.putString(ARG_DINOSAUR_NAME, dinosaur.getName());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDinosaur = Dinosaur.findById(getArguments().getLong(ARG_DINOSAUR_ID));
            if (mDinosaur != null) {
                mDinosaur.setName(getArguments().getString(ARG_DINOSAUR_NAME, null));
            }
        }

        if (mDinosaur == null) {
            mDinosaur = new Dinosaur();
            mIsNew = true;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.comp_dinosaur_editor_dialog, null);

        final TextInputLayout editNameLayout = view.findViewById(R.id.edit_layout_name);
        final TextInputEditText editName = view.findViewById(R.id.edit_name);
        editName.setText(mDinosaur.getName());

        String[] names = new String[] {
                Period.CRETACEOUS.name(),
                Period.JURASSIC.name(),
                Period.TRIASSIC.name()
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, names);
        final Spinner spinner = view.findViewById(R.id.spinner_period_list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Dinosaur Data")
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDinosaur.setPeriod(Period.valueOf((String) spinner.getSelectedItem()));
                        mDinosaur.setName(editName.getText().toString());
                        boolean wasSuccessful = mDinosaur.save(new AbsValidator.ValidationErrorListener() {
                            @Override
                            public void onValidationError(int validatorTag, Set<Integer> errors) {
                                if (errors.contains(DinosaurTableContract.ERROR_NAME_CANNOT_BE_EMPTY)) {
                                    editNameLayout.setError("Give a dinosaur's name.");
                                }
                            }
                        });

                        if (wasSuccessful) {
                            mListener.onSaveSuccessful(mDinosaur, mIsNew);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnSaveSuccessfulListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnSaveSuccessfulListener.class.getName());
        }
    }
}
