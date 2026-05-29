package com.degifetise.madguzoethiopiamobapp;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.Random;

public class CulinaryFactDialogFragment extends DialogFragment {

    private final String[] facts = {
        "Injera is a sourdough-risen flatbread with a unique, slightly spongy texture.",
        "Ethiopian coffee ceremony is an important cultural ritual.",
        "Berbere is a key spice blend in many Ethiopian dishes.",
        "Teff is the smallest grain in the world but packed with nutrition.",
        "Doro Wat is a spicy chicken stew often served on special occasions."
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String randomFact = facts[new Random().nextInt(facts.length)];

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.culinary_fact_title)
                .setMessage(randomFact)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dismiss())
                .create();
    }
}