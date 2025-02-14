package com.example.ma02mibu.fragments.employees;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ma02mibu.R;
import com.example.ma02mibu.activities.CloudStoreUtil;
import com.example.ma02mibu.adapters.EmployeeListAdapter;
import com.example.ma02mibu.adapters.EventExpandableListAdapter;
import com.example.ma02mibu.databinding.FragmentEmployeeRegistrationBinding;
import com.example.ma02mibu.databinding.FragmentEmployeeWorkCalendarBinding;
import com.example.ma02mibu.model.Employee;
import com.example.ma02mibu.model.EventModel;
import com.example.ma02mibu.model.OurNotification;
import com.example.ma02mibu.model.WorkSchedule;
import com.example.ma02mibu.model.WorkTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeWorkCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeWorkCalendarFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Employee mEmployee;
    private ArrayList<EventModel> eventModels;
    private String mParam2;

    private int currentYear, currentMonth, currentDayOfMonth;
    private FragmentEmployeeWorkCalendarBinding binding;

    public EmployeeWorkCalendarFragment() {
        // Required empty public constructor
    }

    public static EmployeeWorkCalendarFragment newInstance(Employee param1, String param2) {
        EmployeeWorkCalendarFragment fragment = new EmployeeWorkCalendarFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmployee = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private TextView tvSelectedWeek;
    private TextView eventSelectedDay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmployeeWorkCalendarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        tvSelectedWeek = view.findViewById(R.id.tvSelectedWeek);
        Button btnPickWeek = view.findViewById(R.id.btnPickWeek);
        btnPickWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(view);
            }
        });
        eventSelectedDay = view.findViewById(R.id.eventSelectedDate);
        Button btnPickEventDate = view.findViewById(R.id.btnNewEventDate);
        btnPickEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog2();
            }
        });
        Button btnNewEvent = view.findViewById(R.id.btnNewEvent);
        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEvent();
            }
        });
        loadEventModels();
        return view;
    }

    private void loadEventModels() {
        CloudStoreUtil.getEventModels(mEmployee.getEmail(), new CloudStoreUtil.EventModelsCallback() {
            @Override
            public void onSuccess(ArrayList<EventModel> itemList) {
                eventModels = new ArrayList<>(itemList);
            }

            @Override
            public void onFailure(Exception e) {
                eventModels = new ArrayList<>();
                System.err.println("Error fetching documents: " + e.getMessage());
            }
        });
    }

    private void addNewEvent(){
        String name = binding.eName.getText().toString();
        String fTime = binding.eTimeFrom.getText().toString();
        String tTime = binding.eTimeTo.getText().toString();
        String date = binding.eventSelectedDate.getText().toString();
        if(name.isEmpty() || fTime.isEmpty() || tTime.isEmpty()){
            Toast.makeText(getContext(), "Enter data first.", Toast.LENGTH_LONG).show();
            return;
        }
        if(date.equals(" . ")){
            Toast.makeText(getContext(), "Choose date first.", Toast.LENGTH_LONG).show();
            return;
        }
        String[] dayHM = extraxtHM(fTime);
        if(dayHM.length == 2){
            for (int i = 0; i < 2; i ++){
                if (dayHM[i].length() != 2) {
                    Toast.makeText(getContext(), "Wrong start time format entry.", Toast.LENGTH_LONG).show();
                    return;
                }
                Integer r = parseIntOrNull(dayHM[i]);
                if(r == null){
                    Toast.makeText(getContext(), "Wrong start time entry.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            int hours1 = Integer.parseInt(dayHM[0]);
            int minutes1 = Integer.parseInt(dayHM[1]);
            Log.i("GGGGGGG", "" + hours1 + " " + minutes1 );
            if(hours1 < 0 || hours1 > 23){
                Toast.makeText(getContext(), "Wrong start time hours entry.", Toast.LENGTH_LONG).show();
                return;
            }
            if(minutes1 < 0 || minutes1 > 59){
                Toast.makeText(getContext(), "Wrong start time minutes entry.", Toast.LENGTH_LONG).show();
                return;
            }
        }else{
            Toast.makeText(getContext(), "Wrong start time format.", Toast.LENGTH_LONG).show();
            return;
        }
        dayHM = extraxtHM(tTime);
        if(dayHM.length == 2){
            for (int i = 0; i < 2; i ++){
                if (dayHM[i].length() != 2) {
                    Toast.makeText(getContext(), "Wrong end time format entry.", Toast.LENGTH_LONG).show();
                    return;
                }
                Integer r = parseIntOrNull(dayHM[i]);
                if(r == null){
                    Toast.makeText(getContext(), "Wrong end time entry.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            int hours1 = Integer.parseInt(dayHM[0]);
            int minutes1 = Integer.parseInt(dayHM[1]);
            Log.i("GGGGGGG", "" + hours1 + " " + minutes1 );
            if(hours1 < 0 || hours1 > 23){
                Toast.makeText(getContext(), "Wrong end time hours entry.", Toast.LENGTH_LONG).show();
                return;
            }
            if(minutes1 < 0 || minutes1 > 59){
                Toast.makeText(getContext(), "Wrong end time minutes entry.", Toast.LENGTH_LONG).show();
                return;
            }
        }else{
            Toast.makeText(getContext(), "Wrong end time format.", Toast.LENGTH_LONG).show();
            return;
        }
        EventModel eventModel = new EventModel(name, date, fTime, tTime, "taken", mEmployee.getEmail());
        String[] dsA = date.split("-");
        String ds = dsA[0];
        String ms = dsA[1];
        String ys = dsA[2];
        int d = Integer.parseInt(ds);
        int m = Integer.parseInt(ms);
        int y = Integer.parseInt(ys);
        LocalDate ld = LocalDate.of(y, m, d);
        DayOfWeek weekday = ld.getDayOfWeek();
        WorkTime wt = mEmployee.findActiveWorkScheduleAlt(ld).ScheduleForThisDay(weekday);
        if(wt == null){
            Toast.makeText(getContext(), "Not working that day.", Toast.LENGTH_LONG).show();
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime ft = LocalTime.parse(fTime,dtf);
        LocalTime wft = LocalTime.parse(wt.getStartTime(),dtf);
        LocalTime et = LocalTime.parse(tTime,dtf);
        LocalTime wet = LocalTime.parse(wt.getEndTime(),dtf);
        if(ft.isAfter(et)){
            Toast.makeText(getContext(), "Start time has to be before end time.", Toast.LENGTH_LONG).show();
            return;
        }
        if(ft.isBefore(wft) || et.isAfter(wet)){
            Toast.makeText(getContext(), "Not in working hours.", Toast.LENGTH_LONG).show();
            return;
        }
        for(EventModel em : eventModels){
            if(em.getDate().equals(date)){
                LocalTime t1 = LocalTime.parse(em.getFromTime(),dtf);
                LocalTime t2 = LocalTime.parse(em.getToTime(),dtf);
                if(t1.isBefore(et) && ft.isBefore(t2)){
                    Toast.makeText(getContext(), "You have an event in that time.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        CloudStoreUtil.insertEventModel(eventModel);
        eventModels.add(eventModel);
        Toast.makeText(getContext(), "Event created.", Toast.LENGTH_LONG).show();
        OurNotification notification = new OurNotification(mEmployee.getEmail(), "New event","Event for you " + eventModel.getDate() + " " + eventModel.getFromTime() + "-" + eventModel.getToTime(), "notRead");
        CloudStoreUtil.insertNotification(notification);
        changeViews();
    }
    public Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private String[] extraxtHM(String hoursEntry){
        String endH, endM;
        String[] endHM = hoursEntry.split(":");
        if(endHM.length == 2){
            endH = endHM[0];
            endM = endHM[1];
        }else{
            return new String[]{};
        }
        return new String[]{endH, endM};
    }

    private void showDatePickerDialog(View parentV) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Convert selected date to week number
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        currentDayOfMonth = dayOfMonth;
                        currentMonth = month;
                        currentYear = year;
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        int weekNumber = selectedCalendar.get(Calendar.WEEK_OF_YEAR);
                        tvSelectedWeek.setText(" number : " + weekNumber + " ");

                        selectedCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                        selectedCalendar.set(Calendar.DAY_OF_WEEK, selectedCalendar.getFirstDayOfWeek());

                        ExpandableListView expandableListView = parentV.findViewById((R.id.expandableListView));
                        HashMap<String, List<EventModel>> expandableListDetail = new HashMap<String, List<EventModel>>();

                        //monday
                        List<EventModel> eventsList1 = new ArrayList<EventModel>();
                        for(EventModel em : eventModels){
                            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                                eventsList1.add(em);
                            }
                        }
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        LocalDate dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);

                        expandableListDetail.put(df.format(selectedCalendar.getTime()) + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.MONDAY), eventsList1);
                        //tuesday
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        List<EventModel> eventsList2 = new ArrayList<EventModel>();
//                        eventsList2.add(new EventModel("dogadjaj1", LocalDate.of(2024, 4, 18).toString(), LocalTime.of(10,30).toString(), LocalTime.of(11,0).toString(),"taken"));
                        for(EventModel em : eventModels){
                            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                                eventsList2.add(em);
                            }
                        }
                        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
                        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.TUESDAY), eventsList2);

                        //wed
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        List<EventModel> eventsList3 = new ArrayList<EventModel>();
//                        eventsList3.add(new EventModel("dogadjaj1", LocalDate.of(2024, 4, 18).toString(), LocalTime.of(10,30).toString(), LocalTime.of(11,0).toString(),"booked"));
                        for(EventModel em : eventModels){
                            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                                eventsList3.add(em);
                            }
                        }
                        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
                        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.WEDNESDAY), eventsList3);
                        //thur
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        List<EventModel> eventsList4 = new ArrayList<EventModel>();
                        for(EventModel em : eventModels){
                            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                                eventsList4.add(em);
                            }
                        }
                        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
                        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.THURSDAY), eventsList4);
                        //friday
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        List<EventModel> eventsList5 = new ArrayList<EventModel>();
//                        eventsList5.add(new EventModel("dogadjaj1", LocalDate.of(2024, 4, 18).toString(), LocalTime.of(10,30).toString(), LocalTime.of(11,0).toString(),"booked"));
                        for(EventModel em : eventModels){
                            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                                eventsList5.add(em);
                            }
                        }
                        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
                        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.FRIDAY), eventsList5);
                        //sat
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        List<EventModel> eventsList6 = new ArrayList<EventModel>();
                        for(EventModel em : eventModels){
                            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                                eventsList6.add(em);
                            }
                        }
                        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
                        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.SATURDAY), eventsList6);
                        //sun
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        List<EventModel> eventsList7 = new ArrayList<EventModel>();
                        for(EventModel em : eventModels){
                            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                                eventsList7.add(em);
                            }
                        }
                        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
                        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.SUNDAY), eventsList7);

                        List<String> expandableListTitle;
                        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                        Collections.sort(expandableListTitle);
                        EventExpandableListAdapter adapter = new EventExpandableListAdapter( getActivity(), expandableListTitle, expandableListDetail);
                        expandableListView.setAdapter(adapter);
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }
    private void changeViews(){
        // Convert selected date to week number
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(currentYear, currentMonth, currentDayOfMonth);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        int weekNumber = selectedCalendar.get(Calendar.WEEK_OF_YEAR);
        tvSelectedWeek.setText(" number : " + weekNumber + " ");

        selectedCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        selectedCalendar.set(Calendar.DAY_OF_WEEK, selectedCalendar.getFirstDayOfWeek());

        ExpandableListView expandableListView = binding.expandableListView;
        HashMap<String, List<EventModel>> expandableListDetail = new HashMap<String, List<EventModel>>();

        //monday
        List<EventModel> eventsList1 = new ArrayList<EventModel>();
        for(EventModel em : eventModels){
            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                eventsList1.add(em);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
        expandableListDetail.put(df.format(selectedCalendar.getTime()) + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.MONDAY), eventsList1);
        //tuesday
        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
        List<EventModel> eventsList2 = new ArrayList<EventModel>();
//                        eventsList2.add(new EventModel("dogadjaj1", LocalDate.of(2024, 4, 18).toString(), LocalTime.of(10,30).toString(), LocalTime.of(11,0).toString(),"taken"));
        for(EventModel em : eventModels){
            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                eventsList2.add(em);
            }
        }
        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.TUESDAY), eventsList2);

        //wed
        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
        List<EventModel> eventsList3 = new ArrayList<EventModel>();
//                        eventsList3.add(new EventModel("dogadjaj1", LocalDate.of(2024, 4, 18).toString(), LocalTime.of(10,30).toString(), LocalTime.of(11,0).toString(),"booked"));
        for(EventModel em : eventModels){
            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                eventsList3.add(em);
            }
        }
        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.WEDNESDAY), eventsList3);
        //thur
        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
        List<EventModel> eventsList4 = new ArrayList<EventModel>();
        for(EventModel em : eventModels){
            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                eventsList4.add(em);
            }
        }
        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.THURSDAY), eventsList4);
        //friday
        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
        List<EventModel> eventsList5 = new ArrayList<EventModel>();
//                        eventsList5.add(new EventModel("dogadjaj1", LocalDate.of(2024, 4, 18).toString(), LocalTime.of(10,30).toString(), LocalTime.of(11,0).toString(),"booked"));
        for(EventModel em : eventModels){
            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                eventsList5.add(em);
            }
        }
        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.FRIDAY), eventsList5);
        //sat
        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
        List<EventModel> eventsList6 = new ArrayList<EventModel>();
        for(EventModel em : eventModels){
            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                eventsList6.add(em);
            }
        }
        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.SATURDAY), eventsList6);
        //sun
        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
        List<EventModel> eventsList7 = new ArrayList<EventModel>();
        for(EventModel em : eventModels){
            if(df.format(selectedCalendar.getTime()).equals(em.getDate())){
                eventsList7.add(em);
            }
        }
        dM1 = LocalDate.parse(df.format(selectedCalendar.getTime()), formatter);
        expandableListDetail.put(df.format(selectedCalendar.getTime())  + "\t    " + mEmployee.findActiveWorkScheduleAlt(dM1).ScheduleForDay(DayOfWeek.SUNDAY), eventsList7);

        List<String> expandableListTitle;
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        Collections.sort(expandableListTitle);
        EventExpandableListAdapter adapter = new EventExpandableListAdapter( getActivity(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(adapter);
    }
    private void showDatePickerDialog2() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Convert selected date to week number
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        int monthN = month + 1;
                        if(monthN < 10){
                            if(dayOfMonth < 10){
                                eventSelectedDay.setText("0" + dayOfMonth + "-" + "0" + monthN + "-" + year);
                            }else {
                                eventSelectedDay.setText(dayOfMonth + "-" + "0" + monthN + "-" + year);
                            }
                        }else{
                            if(dayOfMonth < 10){
                                eventSelectedDay.setText("0" + dayOfMonth + "-" + monthN + "-" + year);
                            }else{
                                eventSelectedDay.setText(dayOfMonth + "-" + monthN + "-" + year);
                            }
                        }
                    }
                },
                year,
                month,
                dayOfMonth
        );
        datePickerDialog.show();
    }
}