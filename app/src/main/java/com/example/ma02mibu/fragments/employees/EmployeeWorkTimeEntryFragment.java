package com.example.ma02mibu.fragments.employees;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ma02mibu.FragmentTransition;
import com.example.ma02mibu.R;
import com.example.ma02mibu.activities.CloudStoreUtil;
import com.example.ma02mibu.databinding.FragmentEmployeeDetailsBinding;
import com.example.ma02mibu.databinding.FragmentEmployeeRegistrationBinding;
import com.example.ma02mibu.databinding.FragmentEmployeeWorkTimeEntryBinding;
import com.example.ma02mibu.model.Employee;
import com.example.ma02mibu.model.WorkSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeWorkTimeEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeWorkTimeEntryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Employee mEmployee;
    private String ownerRefId;

    public EmployeeWorkTimeEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmployeeWorkTimeEntryFragment.
     */

    private FragmentEmployeeWorkTimeEntryBinding binding;
    public static EmployeeWorkTimeEntryFragment newInstance(Employee param1, String param2) {
        EmployeeWorkTimeEntryFragment fragment = new EmployeeWorkTimeEntryFragment();
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
            ownerRefId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEmployeeWorkTimeEntryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        eventSelectedDayStart = view.findViewById(R.id.eventSelectedStart);
        Button btnPickStartDate = view.findViewById(R.id.btnNewEventStartDate);
        btnPickStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog2();
            }
        });
        eventSelectedDayEnd = view.findViewById(R.id.eventSelectedEnd);
        Button btnPickEndDate = view.findViewById(R.id.btnNewEventEndDate);
        btnPickEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog1();
            }
        });

        Button btnRegister = binding.btnRegister;
        btnRegister.setOnClickListener(v -> {
            try {
                addWorkingHours();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return view;
    }

    private void addWorkingHours() throws InterruptedException{
        String mondayHours = binding.etMondayHours.getText().toString();
        String tuesdayHours = binding.etTuesdayHours.getText().toString();
        String wedHours = binding.etWednesdayHours.getText().toString();
        String thurHours = binding.etThursdayHours.getText().toString();
        String friHours = binding.etFridayHours.getText().toString();
        String satHours = binding.SaturdayHours.getText().toString();
        String sunHours = binding.etSundayHours.getText().toString();
        if(mondayHours.isEmpty() && tuesdayHours.isEmpty() && wedHours.isEmpty() && thurHours.isEmpty() && friHours.isEmpty()
                && satHours.isEmpty() && sunHours.isEmpty()){
            alertShow("All");
            return;
        }
        WorkSchedule customWorkSchedule = new WorkSchedule();
        if(!mondayHours.isEmpty()){
            String[] dayHM = extraxtHM(mondayHours);
            if(dayHM.length == 4){
                if(!checkValid(dayHM)){
                    return;
                }
                LocalTime t1 = LocalTime.of(Integer.parseInt(dayHM[0]), Integer.parseInt(dayHM[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(dayHM[2]), Integer.parseInt(dayHM[3]));
                if(t1.isAfter(t2)){
                    Toast.makeText(getContext(), "Monday working hours are wrong.", Toast.LENGTH_LONG).show();
                    return;
                }
                customWorkSchedule.setWorkTime(DayOfWeek.MONDAY, t1, t2);
            }else{
                alertShow("Monday");
                return;
            }
        }else{
            customWorkSchedule.setWorkTime(DayOfWeek.MONDAY, null, null);
        }
        if(!tuesdayHours.isEmpty()){
            String[] dayHM = extraxtHM(tuesdayHours);
            if(dayHM.length == 4){
                if(!checkValid(dayHM)){
                    return;
                }
                LocalTime t1 = LocalTime.of(Integer.parseInt(dayHM[0]), Integer.parseInt(dayHM[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(dayHM[2]), Integer.parseInt(dayHM[3]));
                if(t1.isAfter(t2)){
                    Toast.makeText(getContext(), "Tuesday working hours are wrong.", Toast.LENGTH_LONG).show();
                    return;
                }
                customWorkSchedule.setWorkTime(DayOfWeek.TUESDAY, t1, t2);
            }else{
                alertShow("Tuesday");
                return;
            }
        }else{
            customWorkSchedule.setWorkTime(DayOfWeek.TUESDAY, null, null);
        }
        if(!wedHours.isEmpty()){
            String[] dayHM = extraxtHM(wedHours);
            if(dayHM.length == 4){
                if(!checkValid(dayHM)){
                    return;
                }
                LocalTime t1 = LocalTime.of(Integer.parseInt(dayHM[0]), Integer.parseInt(dayHM[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(dayHM[2]), Integer.parseInt(dayHM[3]));
                if(t1.isAfter(t2)){
                    Toast.makeText(getContext(), "Wednesday working hours are wrong.", Toast.LENGTH_LONG).show();
                    return;
                }
                customWorkSchedule.setWorkTime(DayOfWeek.WEDNESDAY, t1, t2);
            }else{
                alertShow("Wednesday");
                return;
            }
        }else{
            customWorkSchedule.setWorkTime(DayOfWeek.WEDNESDAY, null, null);
        }
        if(!thurHours.isEmpty()){
            String[] dayHM = extraxtHM(thurHours);
            if(dayHM.length == 4){
                if(!checkValid(dayHM)){
                    return;
                }
                LocalTime t1 = LocalTime.of(Integer.parseInt(dayHM[0]), Integer.parseInt(dayHM[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(dayHM[2]), Integer.parseInt(dayHM[3]));
                if(t1.isAfter(t2)){
                    Toast.makeText(getContext(), "Thursday working hours are wrong.", Toast.LENGTH_LONG).show();
                    return;
                }
                customWorkSchedule.setWorkTime(DayOfWeek.THURSDAY, t1, t2);
            }else{
                alertShow("Thursday");
                return;
            }
        }else{
            customWorkSchedule.setWorkTime(DayOfWeek.THURSDAY, null, null);
        }
        if(!friHours.isEmpty()){
            String[] dayHM = extraxtHM(friHours);
            if(dayHM.length == 4){
                if(!checkValid(dayHM)){
                    return;
                }
                LocalTime t1 = LocalTime.of(Integer.parseInt(dayHM[0]), Integer.parseInt(dayHM[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(dayHM[2]), Integer.parseInt(dayHM[3]));
                if(t1.isAfter(t2)){
                    Toast.makeText(getContext(), "Friday working hours are wrong.", Toast.LENGTH_LONG).show();
                    return;
                }
                customWorkSchedule.setWorkTime(DayOfWeek.FRIDAY, t1, t2);
            }else{
                alertShow("Friday");
                return;
            }
        }else{
            customWorkSchedule.setWorkTime(DayOfWeek.FRIDAY, null, null);
        }
        if(!satHours.isEmpty()){
            String[] dayHM = extraxtHM(satHours);
            if(dayHM.length == 4){
                if(!checkValid(dayHM)){
                    return;
                }
                LocalTime t1 = LocalTime.of(Integer.parseInt(dayHM[0]), Integer.parseInt(dayHM[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(dayHM[2]), Integer.parseInt(dayHM[3]));
                if(t1.isAfter(t2)){
                    Toast.makeText(getContext(), "Saturday working hours are wrong.", Toast.LENGTH_LONG).show();
                    return;
                }
                customWorkSchedule.setWorkTime(DayOfWeek.SATURDAY, t1, t2);
            }else{
                alertShow("Saturday");
                return;
            }
        }else{
            customWorkSchedule.setWorkTime(DayOfWeek.SATURDAY, null, null);
        }
        if(!sunHours.isEmpty()){
            String[] dayHM = extraxtHM(sunHours);
            if(dayHM.length == 4){
                if(!checkValid(dayHM)){
                    return;
                }
                LocalTime t1 = LocalTime.of(Integer.parseInt(dayHM[0]), Integer.parseInt(dayHM[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(dayHM[2]), Integer.parseInt(dayHM[3]));
                if(t1.isAfter(t2)){
                    Toast.makeText(getContext(), "Sunday working hours are wrong.", Toast.LENGTH_LONG).show();
                    return;
                }
                customWorkSchedule.setWorkTime(DayOfWeek.SUNDAY, t1, t2);
            }else{
                alertShow("Sunday");
                return;
            }
        }else{
            customWorkSchedule.setWorkTime(DayOfWeek.SUNDAY, null, null);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dM1 = LocalDate.parse(eventSelectedDayStart.getText().toString(), formatter);
        LocalDate dM2 = LocalDate.parse(eventSelectedDayEnd.getText().toString(), formatter);
        if(dM2.isBefore(dM1)){
            Toast.makeText(getContext(), "End date has to be after start.", Toast.LENGTH_LONG).show();
            return;
        }

        customWorkSchedule.setStartDay(eventSelectedDayStart.getText().toString());
        customWorkSchedule.setEndDay(eventSelectedDayEnd.getText().toString());
        mEmployee.setSchedule(customWorkSchedule);
        CloudStoreUtil.updateEmployeesWS(mEmployee, new CloudStoreUtil.UpdateItemCallback() {
            @Override
            public void onSuccess() {
                // Item updated successfully
                System.out.println("Item updated!");
                FragmentTransition.to(EmployeeListFragment.newInstance(), getActivity(),true, R.id.scroll_employees_list, "EmployeeWTE");
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the failure (e.g., show an error message)
                System.err.println("Error updating item: " + e.getMessage());
            }
        });
//        Thread.sleep(600);
    }

    private boolean checkValid(String[] dayHM){
        for (int i = 0; i < 4; i ++){
            if (dayHM[i].length() != 2) {
                Toast.makeText(getContext(), "Wrong time format entry.", Toast.LENGTH_LONG).show();
                return false;
            }
            Integer r = parseIntOrNull(dayHM[i]);
            if(r == null){
                Toast.makeText(getContext(), "Wrong time entry.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        int hours1 = Integer.parseInt(dayHM[0]);
        int minutes1 = Integer.parseInt(dayHM[1]);
        if(hours1 < 0 || hours1 > 23){
            Toast.makeText(getContext(), "Wrong time hours entry.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(minutes1 < 0 || minutes1 > 59){
            Toast.makeText(getContext(), "Wrong time minutes entry.", Toast.LENGTH_LONG).show();
            return false;
        }
        hours1 = Integer.parseInt(dayHM[2]);
        minutes1 = Integer.parseInt(dayHM[3]);
        if(hours1 < 0 || hours1 > 23){
            Toast.makeText(getContext(), "Wrong time hours entry.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(minutes1 < 0 || minutes1 > 59){
            Toast.makeText(getContext(), "Wrong time minutes entry.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void alertShow(String day) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Wrong input");
        alertDialog.setMessage(day + " working hours are in wrong format! \nPlease use HH:mm-HH:mm or leave empty");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // No action needed here since the button does nothing.
            }
        });
        alertDialog.show();
    }

    private String[] extraxtHM(String hoursEntry){
        String start, end, startH, startM, endH, endM;
        String[] times = hoursEntry.split("-");
        if(times.length == 2){
            start = times[0];
            end = times[1];
        }else{
            return new String[]{};
        }
        String[] startHM = start.split(":");
        if(startHM.length == 2){
            startH = startHM[0];
            startM = startHM[1];
        }else{
            return new String[]{};
        }
        String[] endHM = end.split(":");
        if(endHM.length == 2){
            endH = endHM[0];
            endM = endHM[1];
        }else{
            return new String[]{};
        }
        return new String[]{startH, startM, endH, endM};
    }

    private TextView eventSelectedDayStart;
    private TextView eventSelectedDayEnd;
    private void showDatePickerDialog1() {
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
                                eventSelectedDayEnd.setText("0" + dayOfMonth + "-" + "0" + monthN + "-" + year);
                            }else {
                                eventSelectedDayEnd.setText(dayOfMonth + "-" + "0" + monthN + "-" + year);
                            }
                        }else{
                            if(dayOfMonth < 10){
                                eventSelectedDayEnd.setText("0" + dayOfMonth + "-" + monthN + "-" + year);
                            }else{
                                eventSelectedDayEnd.setText(dayOfMonth + "-" + monthN + "-" + year);
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
                                eventSelectedDayStart.setText("0" + dayOfMonth + "-" + "0" + monthN + "-" + year);
                            }else {
                                eventSelectedDayStart.setText(dayOfMonth + "-" + "0" + monthN + "-" + year);
                            }
                        }else{
                            if(dayOfMonth < 10){
                                eventSelectedDayStart.setText("0" + dayOfMonth + "-" + monthN + "-" + year);
                            }else{
                                eventSelectedDayStart.setText(dayOfMonth + "-" + monthN + "-" + year);
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