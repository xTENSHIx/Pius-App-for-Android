package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.calendar.DayItem;
import com.rmkrings.activities.R;

import java.util.ArrayList;

public class CalendarDateListAdapter extends RecyclerView.Adapter<CalendarDateListAdapter.DateListViewHolder> {

    private final ArrayList<DayItem> dateList;

    static class DateListViewHolder extends RecyclerView.ViewHolder {

        final TextView dateView;
        final TextView eventView;
        DateListViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.date);
            eventView = itemView.findViewById(R.id.event);
        }
    }

    public CalendarDateListAdapter(ArrayList<DayItem> dateList) {
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public DateListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout v = (ConstraintLayout)LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.date_list_item, viewGroup, false);
        return new DateListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DateListViewHolder dateListViewHolder, int i) {
        DayItem dayItem = dateList.get(i);
        dateListViewHolder.dateView.setText(dayItem.getDay());
        dateListViewHolder.eventView.setText(dayItem.getEvent());
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }
}
