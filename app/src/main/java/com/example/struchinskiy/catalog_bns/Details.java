package com.example.struchinskiy.catalog_bns;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Details extends Fragment {
    private String fio, job, podrazdel, short_tel, tel, mobile, e_mail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Информация о сотруднике");
        if (getArguments() != null) {
            Person person = (Person) getArguments().getSerializable("person");
            if (person != null) {
                fio = person.getFio();
                job = person.getJob();
                podrazdel = person.getPodrazdel();
                short_tel = person.getShort_tel();
                tel = person.getTel();
                mobile = person.getMobile();
                e_mail = person.getE_mail();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.details, null);
        ImageView imageViewIcon = view.findViewById(R.id.imageView);
        imageViewIcon.setColorFilter(getResources().getColor(android.R.color.holo_orange_light));

        TextView tvFio = view.findViewById(R.id.tvFIO);
        TextView tvJob = view.findViewById(R.id.tvJob);
        TextView tvPodrazdel = view.findViewById(R.id.tvPodrazdel);
        TextView tvSort_tel = view.findViewById(R.id.tvShort_tel);
        TextView tvTel = view.findViewById(R.id.tvTel);
        TextView tvMobile = view.findViewById(R.id.tvMobile);
        TextView tvE_mail = view.findViewById(R.id.tvEmail);
        if (mobile.isEmpty()) {
            tvMobile.setVisibility(View.INVISIBLE);
        }
        if (e_mail.isEmpty()) {
            imageViewIcon.setVisibility(View.INVISIBLE);
        }
        tvFio.setText(fio);
        tvJob.setText(job);
        tvPodrazdel.setText(podrazdel);
        tvSort_tel.setText(short_tel);
        tvTel.setText(tel);
        tvMobile.setText(mobile);
        tvE_mail.setText(e_mail);

        tvMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mobile.isEmpty()) {
                    mobile = mobile.replaceAll("[^0-9|]", "");
                    String[] phones = mobile.split("\\|");
                    AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                    adb.setSingleChoiceItems(phones, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListView lv = ((AlertDialog) dialog).getListView();
                            String phone = (String) lv.getAdapter().getItem(which);
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:8" + phone));
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    })
                            .setTitle("Выберите номер")
                            .create()
                            .show();
                }
            }
        });

        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!e_mail.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + e_mail));
                    startActivity(Intent.createChooser(emailIntent, "Отправить сообщение"));
                }
            }
        });
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
         MenuItem item = menu.findItem(R.id.search);
         item.setVisible(false);
    }
}
