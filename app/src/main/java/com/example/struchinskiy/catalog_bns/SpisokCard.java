package com.example.struchinskiy.catalog_bns;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class SpisokCard extends Fragment implements CardClick {
    private List<Person> people;
    private String type;
    private SpisokAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        String search = "";
        type = "";
        if (getArguments() != null) {
            type = getArguments().getString("type");
            search = getArguments().getString("search");
        }
        if (type.equals("birth_day")) {
            setHasOptionsMenu(true);
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle("Дни рождения");
        }
        QueryList queryList = new QueryList(getContext(), type);
        people = queryList.execQuery(type, search);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spisok, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new SpisokAdapter(people, type);
        adapter.setCardClick(this);
        recyclerView.setAdapter(adapter);
        if (adapter.getItemCount() == 0)
            Toast.makeText(getActivity(), "Ничего не найдено.", Toast.LENGTH_LONG).show();
        return view;
    }


    @Override
    public void onCardClick(Person person) {
        Fragment fragment = new Details();
        Bundle bundle = new Bundle();
        bundle.putSerializable("person", person);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }


    public void onTextSubmit(String s) {
        adapter.getFilter().filter(s);
    }

    public void onTextChange(String s) {
        adapter.getFilter().filter(s);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }
}
