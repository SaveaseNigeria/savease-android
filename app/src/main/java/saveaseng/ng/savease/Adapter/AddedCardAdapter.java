package saveaseng.ng.savease.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.Model.VoucherModel;
import saveaseng.ng.savease.R;

public class AddedCardAdapter extends RecyclerView.Adapter<AddedCardAdapter.ViewHolder> {

    ArrayList<VoucherModel> voucherModels;
    Context context;

    public AddedCardAdapter(ArrayList<VoucherModel> voucherModels, Context context) {
        this.voucherModels = voucherModels;
        this.context = context;
    }

    @NonNull
    @Override
    public AddedCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_added_card_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedCardAdapter.ViewHolder viewHolder, final int i) {

        VoucherModel voucherModel = voucherModels.get(i);
        viewHolder.amount.setText(voucherModel.getCardType());
        viewHolder.cardImage.setImageResource(voucherModel.getImageId());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voucherModels.remove(i);
                notifyItemRemoved(i);
                notifyDataSetChanged();
                notifyItemRangeChanged(i,voucherModels.size());

            }
        });

        viewHolder.quantity.setText(voucherModel.getQuntity());

    }

    @Override
    public int getItemCount() {
        return voucherModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        ImageButton delete;
        TextView amount,quantity;
        View mView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            cardImage = (ImageView)mView.findViewById(R.id.imgCardAdded);
            delete = (ImageButton)mView.findViewById(R.id.imgRemoveAdded);
            amount = (TextView)mView.findViewById(R.id.imgCardAmountAdded);
            quantity = (TextView)mView.findViewById(R.id.imgCardQuantity);


        }
    }
}
