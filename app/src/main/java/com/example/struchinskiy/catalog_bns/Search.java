package com.example.struchinskiy.catalog_bns;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {
    private String search_type;
    EditText dolzhnost, fio;
    Spinner filial, otdel;
    private QueryList queryList;
    private int pos_otdel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Расширенный поиск");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, null);
        otdel = view.findViewById(R.id.podrazdel);
        filial = view.findViewById(R.id.filial);
        fio = view.findViewById(R.id.et1);
        dolzhnost = view.findViewById(R.id.et);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClick();
            }
        });
        queryList = new QueryList(getActivity(), "filial");

        List<String> filials = queryList.getSpinner("filial", "");
        filials.add(0, "По всем");
        ArrayAdapter<String> f_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, filials);
        f_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        filial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    List<String> podrazdels = queryList.getSpinner("otdel", String.valueOf(position));
                    podrazdels.add(0, "По всем");
                    ArrayAdapter<String> p_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, podrazdels);
                    p_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    otdel.setAdapter(p_adapter);
                if (view != null) {
                    if (pos_otdel != 0) {
                        otdel.setSelection(pos_otdel);
                        pos_otdel = 0;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filial.setAdapter(f_adapter);
        return view;
    }


    public void searchClick() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);

        pos_otdel = otdel.getSelectedItemPosition();
        Fragment fragment = new SpisokCard();
        Bundle bundle = new Bundle();
        bundle.putString("type", "ext_search");
       // bundle.putString("filial", String.valueOf(filial.getSelectedItemPosition()));
       // bundle.putString("otdel", ((String) otdel.getSelectedItem()).toUpperCase());
       // bundle.putString("dolzhnost", dolzhnost.getText().toString().toUpperCase());
       // bundle.putString("fio", fio.getText().toString().toUpperCase());
        StringBuilder builder = new StringBuilder();
        if (filial.getSelectedItemPosition() != 0)
            builder.append("f.id = ").append(String.valueOf(filial.getSelectedItemPosition())).append(" and ");
        if (otdel.getSelectedItemPosition() != 0)
            builder.append("o.name = '").append(otdel.getSelectedItem()).append("' and ");
        builder.append("upper(s.job) like '%").append(dolzhnost.getText().toString().toUpperCase()).append("%'");
        builder.append(" and upper(s.fio) like '%").append(fio.getText().toString().toUpperCase()).append("%'");
        bundle.putString("search", builder.toString());
       // String search = text.getText().toString().toLowerCase();
        /*if (Build.VERSION.SDK_INT < 23 ){
            if (search_type.equals("fio"))
             search = WordUtils.capitalizeFully(search);
            if (search_type.equals("podrazdel"))
                search = search.substring(0,1).toUpperCase() + search.substring(1);
        }
        bundle.putString("search", search);*/
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }
}
