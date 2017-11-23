package com.pugfish1992.sample.ui;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pugfish1992.sample.R;
import com.pugfish1992.sample.data.Dinosaur;
import com.pugfish1992.sample.data.Period;

import java.util.List;

/**
 * Created by daichi on 11/23/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {

    SortedList<Dinosaur> mDinosaurs;
    private CardHolder.OnItemClickListener mOnItemClickListener;

    public CardAdapter(List<Dinosaur> dinosaurs, CardHolder.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mDinosaurs = new SortedList<>(Dinosaur.class, new SortedList.Callback<Dinosaur>() {
            @Override
            public int compare(Dinosaur o1, Dinosaur o2) {
                return Long.valueOf(o1.getId()).compareTo(o2.getId());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Dinosaur oldItem, Dinosaur newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Dinosaur item1, Dinosaur item2) {
                return item1.getId() == item2.getId();
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
        mDinosaurs.addAll(dinosaurs);
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dinosaur_card, parent,false);
        return new CardHolder(view,
                new CardHolder.OnIconClickListener() {
                    @Override
                    public void onIconClick(int position) {
                        Dinosaur dinosaur = mDinosaurs.get(position);
                        dinosaur.setIsLiked(!dinosaur.isLiked());
                        dinosaur.save();
                        CardAdapter.this.notifyItemChanged(position);
                    }
                },
                mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(final CardHolder holder, int position) {
        final Dinosaur dinosaur = mDinosaurs.get(position);
        holder.bind(dinosaur);
    }

    @Override
    public int getItemCount() {
        return mDinosaurs.size();
    }

    public void addOrUpdateItem(@NonNull Dinosaur dinosaur) {
        mDinosaurs.add(dinosaur);
    }

    public void removeItemAt(int position) {
        mDinosaurs.removeItemAt(position);
    }

    public Dinosaur getItemAt(int position) {
        return mDinosaurs.get(position);
    }

    static class CardHolder extends RecyclerView.ViewHolder {

        private static final int COLOR_ACTIVE_ICON = Color.parseColor("#ff4081");
        private static final int COLOR_INACTIVE_ICON = Color.parseColor("#9d9d9d");

        interface OnIconClickListener {
            void onIconClick(int position);
        }

        interface OnItemClickListener {
            void onItemClick(int position);
        }

        private final OnItemClickListener mOnItemClickListener;
        private final OnIconClickListener mOnIconClickListener;
        private final TextView mName;
        private final TextView mPeriod;
        private final ImageView mIcon;

        CardHolder(View view, OnIconClickListener iconClickListener, OnItemClickListener itemClickListener) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPeriod = view.findViewById(R.id.period);
            mIcon = view.findViewById(R.id.fav_icon);
            mOnIconClickListener = iconClickListener;
            mOnItemClickListener = itemClickListener;

            mIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnIconClickListener.onIconClick(CardHolder.this.getAdapterPosition());
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(CardHolder.this.getAdapterPosition());
                }
            });
        }

        void bind(Dinosaur dinosaur) {
            mName.setText(dinosaur.getName());

            Period period = dinosaur.getPeriod();
            if (period != Period.INVALID_PERIOD) {
                mPeriod.setText("in " + period.toString());
            } else {
                mPeriod.setText(null);
            }

            if (dinosaur.isLiked()) {
                mIcon.setImageResource(R.drawable.ic_favorite);
                mIcon.setColorFilter(COLOR_ACTIVE_ICON);
            } else {
                mIcon.setImageResource(R.drawable.ic_favorite_border);
                mIcon.setColorFilter(COLOR_INACTIVE_ICON);
            }
        }
    }
}
