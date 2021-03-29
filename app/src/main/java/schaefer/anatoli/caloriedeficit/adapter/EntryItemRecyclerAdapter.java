package schaefer.anatoli.caloriedeficit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import schaefer.anatoli.caloriedeficit.R;
import schaefer.anatoli.caloriedeficit.model.EntryItem;

public class EntryItemRecyclerAdapter extends RecyclerView.Adapter<EntryItemRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<EntryItem> entryItemsAL;
    private ItemClickListener mClickListener;


    public EntryItemRecyclerAdapter(Context context, ArrayList<EntryItem> entryItemsAL) {
        this.context = context;
        this.entryItemsAL = entryItemsAL;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        EntryItem entryItem = entryItemsAL.get(position);

        holder.entryItemRowLabelTV.setText(entryItem.getLabel());
        holder.entryItemRowKcalTV.setText(String.valueOf( entryItem.getCalories() ));


        if( position %2 == 0)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.entryItemRowDelete1IV.setVisibility(View.VISIBLE);
            holder.entryItemRowDelete2IV.setVisibility(View.INVISIBLE);


            if(entryItem.isDeleteButtonVisible()){
                holder.entryItemRowDelete1IV.setVisibility(View.VISIBLE);
                holder.entryItemRowDelete2IV.setVisibility(View.INVISIBLE);
            }else{
                holder.entryItemRowDelete1IV.setVisibility(View.INVISIBLE);
                holder.entryItemRowDelete2IV.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
            holder.entryItemRowDelete1IV.setVisibility(View.INVISIBLE);
            holder.entryItemRowDelete2IV.setVisibility(View.VISIBLE);

            if(entryItem.isDeleteButtonVisible()){
                holder.entryItemRowDelete1IV.setVisibility(View.INVISIBLE);
                holder.entryItemRowDelete2IV.setVisibility(View.VISIBLE);
            }else{
                holder.entryItemRowDelete1IV.setVisibility(View.INVISIBLE);
                holder.entryItemRowDelete2IV.setVisibility(View.INVISIBLE);
            }
        }



    }




    public void setDeleteButtonsVisible(boolean isVisible){
        for(int i=0; i < entryItemsAL.size(); i++){
            entryItemsAL.get(i).setDeleteButtonVisible(isVisible);
        }
    }



    @Override
    public int getItemCount() {
        return entryItemsAL.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView entryItemRowKcalTV;
        public TextView entryItemRowLabelTV;

        public ImageView entryItemRowDelete1IV;
        public ImageView entryItemRowDelete2IV;





        public ViewHolder(@NonNull final View itemView, final Context context) {
            super(itemView);
            EntryItemRecyclerAdapter.this.context = context;

            entryItemRowKcalTV = itemView.findViewById(R.id.entryItemRowKcalTV);
            entryItemRowLabelTV = itemView.findViewById(R.id.entryItemRowLabelTV);
            entryItemRowDelete1IV = itemView.findViewById(R.id.entryItemRowDelete1IV);
            entryItemRowDelete2IV = itemView.findViewById(R.id.entryItemRowDelete2IV);

            entryItemRowDelete1IV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if( mClickListener != null)
                            mClickListener.onItemClick(itemView, getAdapterPosition());
                        // Toast.makeText(context,"entryItemAL: ", Toast.LENGTH_SHORT ).show();
                    }
                });


            entryItemRowDelete2IV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if( mClickListener != null)
                            mClickListener.onItemClick(itemView, getAdapterPosition());
                        // Toast.makeText(context,"entryItemAL: ", Toast.LENGTH_SHORT ).show();
                    }
            });



            entryItemRowLabelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            entryItemRowKcalTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    // convenience method for getting data at click position
    public EntryItem getItem(int id) {
        return entryItemsAL.get(id);
    }

    // allows clicks events to be caught
    public  void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
