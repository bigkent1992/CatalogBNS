package com.example.struchinskiy.catalog_bns;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpisokAdapter extends RecyclerView.Adapter<SpisokAdapter.PersonHolder> implements Filterable {
    private List<Person> people;
    private List<Person> peopleFilrered;
    private CardClick cardClick;
    private String typeSearch;

    SpisokAdapter(List<Person> people, String typeSearch) {
        this.people = people;
        this.peopleFilrered = people;
        this.typeSearch = typeSearch;
    }

    public void setCardClick(CardClick cardClick) {
        this.cardClick = cardClick;
    }

    @NonNull
    @Override
    public PersonHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new PersonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonHolder personHolder, int i) {
        final Person person = peopleFilrered.get(i);
        personHolder.fio.setText(person.getFio());
        personHolder.job.setText(person.getJob());
        personHolder.podrazdel.setText(person.getPodrazdel());
        //personHolder.short_tel.setText(person.getShort_tel());
        switch (typeSearch) {
            case "tel":
                personHolder.short_tel.setText(person.getTel());
                break;
            case "mobile":
                personHolder.short_tel.setText(person.getMobile());
                break;
            case "birth_day":
                personHolder.short_tel.setText(person.getBirth_date());
                break;
            default:
                personHolder.short_tel.setText(person.getShort_tel());
                break;
        }
        personHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick.onCardClick(person);
            }
        });
    }

    @Override
    public int getItemCount() {
        return peopleFilrered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint.toString();
                String s;
                if (searchText.isEmpty())
                    peopleFilrered = people;
                else {
                    List<Person> personList = new ArrayList<>();
                    for (Person person : people) {
                        // if (person.getFio() == null || person.getShort_tel() == null || person.getJob() == null)
                        //    s = "1";
                        if (person.getFio().toLowerCase().contains(searchText.toLowerCase()) || person.getShort_tel().contains(searchText)
                                || person.getJob().toLowerCase().contains(searchText.toLowerCase())) {
                            personList.add(person);
                        }
                    }
                    peopleFilrered = personList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = peopleFilrered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                peopleFilrered = (List<Person>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class PersonHolder extends RecyclerView.ViewHolder {
        TextView fio, podrazdel, job, short_tel;

        PersonHolder(@NonNull View itemView) {
            super(itemView);
            fio = itemView.findViewById(R.id.tvFio);
            job = itemView.findViewById(R.id.tvJob);
            podrazdel = itemView.findViewById(R.id.tvPodrazdel);
            short_tel = itemView.findViewById(R.id.tvShort_tel);
        }
    }
}
