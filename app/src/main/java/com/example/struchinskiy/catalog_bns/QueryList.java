package com.example.struchinskiy.catalog_bns;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueryList {
  //  private String search;
    private Context context;
    private String type;

    QueryList(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    private String getSearch(String type, String value) {
        switch (type) {
            case "fio":
                return "select spisok.*, otdel.name || \" / \" || filial.name as podrazdel from spisok\n" +
                        "inner join otdel on otdel.id = spisok.otdel_id inner join filial on filial.id = otdel.filial where spisok.fio like '%" + value + "%' order by spisok.id";
            case "short_tel":
                return "select spisok.*, otdel.name as podrazdel from spisok inner join otdel on otdel.id = spisok.otdel_id where spisok.short_tel like '%" + value + "%'";
            case "tel":
                return "select spisok.*, otdel.name as podrazdel from spisok inner join otdel on otdel.id = spisok.otdel_id where spisok.tel like '%" + value + "%'";
            case "mobile":
                return "select spisok.*, otdel.name as podrazdel from spisok inner join otdel on otdel.id = spisok.otdel_id where spisok.mobile like '%" + value + "%'";
            case "ext_search":
                return "select s.*, o.name || \" / \" || f.name as podrazdel from spisok s\n" +
                        "inner join otdel o on o.id = s.otdel_id inner join filial f on f.id = o.filial where " + value + " order by s.id";
            case "otdel":
                return "select otdel.name from otdel inner join filial on filial.id = otdel.filial where otdel.name <> '' and filial.id = " + value + " order by otdel.name";
            case "filial":
                return "select id, name from filial";
            case "birth_day":
                return "select spisok.*, otdel.name as podrazdel, strftime('%d', spisok.birth_date) || '.' || strftime('%m', spisok.birth_date) as dr\n" +
                        "from spisok inner join otdel on otdel.id = spisok.otdel_id\n" +
                        "where strftime('%m-%d', date(birth_date)) between strftime('%m-%d', date('now')) and strftime('%m-%d', date('now', '+6 day'))\n" +
                        "order by strftime('%m-%d', date(birth_date))";
            default:
                return "";
        }
    }

    public List<Person> execQuery(String type, String value) {
        String fio, job, podrazdel, short_tel, tel, mobile, e_mail, birth_day;
        Person person;
        String searchString = getSearch(type, value);
        List<Person> people = new ArrayList<>();
        Cursor cursor = MainActivity.db.getAllData(searchString);
            if (cursor.moveToFirst()) {
                do {
                    fio = cursor.getString(cursor.getColumnIndex("FIO"));
                    job = cursor.getString(cursor.getColumnIndex("JOB"));
                    podrazdel = cursor.getString(cursor.getColumnIndex("podrazdel"));
                    short_tel = cursor.getString(cursor.getColumnIndex("SHORT_TEL"));
                    tel = cursor.getString(cursor.getColumnIndex("TEL"));
                    mobile = cursor.getString(cursor.getColumnIndex("MOBILE"));
                    e_mail = cursor.getString(cursor.getColumnIndex("E_MAIL"));
                    if (type.equals("birth_day")) {
                        birth_day = cursor.getString(cursor.getColumnIndex("dr"));
                        person = new Person(fio, job, podrazdel, short_tel, tel, mobile, e_mail, birth_day);
                    } else
                        person = new Person(fio, job, podrazdel, short_tel, tel, mobile, e_mail);
                    people.add(person);
                } while (cursor.moveToNext());
            }
        return people;
    }

    public List<String> getSpinner(String type, String value) {
        List<String> filials = new ArrayList<>();
        String searchString = getSearch(type, value);
        Cursor cursor = MainActivity.db.getAllData(searchString);
        if (cursor.moveToFirst()) {
            do {
                String filial = cursor.getString(cursor.getColumnIndex("NAME"));
                filials.add(filial);
            } while (cursor.moveToNext());
        }
        return filials;
    }
}
