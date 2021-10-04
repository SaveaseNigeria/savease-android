package saveaseng.ng.savease.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.Model.VoucherTableModel;
import saveaseng.ng.savease.R;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.MyViewHolder> {

    ArrayList<VoucherTableModel> voucherTableModels;
    Context context;
    private OnItemClicked mItemClickListener;

    public VoucherAdapter(ArrayList<VoucherTableModel> voucherTableModels, OnItemClicked mItemClickListener) {
        this.voucherTableModels = voucherTableModels;
        this.mItemClickListener = mItemClickListener;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_voucher_table_item,viewGroup,false);
        return new MyViewHolder(view,mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        final VoucherTableModel tableModel = voucherTableModels.get(i);

        myViewHolder.voucheramount.setText(tableModel.getAmount());
        myViewHolder.voucherstatus.setText(tableModel.getVoucherStatus());
        myViewHolder.voucherbatch.setText(tableModel.getBatchNo());
        myViewHolder.voucherserial.setText(tableModel.getSerialNumber());
        myViewHolder.voucherpin.setText(tableModel.getVoucherPin());

        if (tableModel.getUsedBy().equalsIgnoreCase("null")){
            myViewHolder.usedby.setText("");
        }else {
            myViewHolder.usedby.setText(tableModel.getUsedBy());
        }

        if (tableModel.getUsedDate().equalsIgnoreCase("null")){
            myViewHolder.useddate.setText("");
        }else {
            myViewHolder.useddate.setText(tableModel.getUsedDate());
        }


    }




    @Override
    public int getItemCount() {
        return voucherTableModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView voucherpin,voucherserial,voucherbatch,voucherstatus,voucheramount,usedby,useddate;
        ImageView more;
        View mView;
        OnItemClicked mListener;
        public MyViewHolder(@NonNull View itemView, OnItemClicked mItemClickListener) {
            super(itemView);
            mView = itemView;
            voucherpin = (TextView)mView.findViewById(R.id.txtVoucherPin);
            voucherserial = (TextView)mView.findViewById(R.id.txtVoucherSerialNumber);
            voucherbatch = (TextView)mView.findViewById(R.id.txtVoucherBatch);
            voucherstatus = (TextView)mView.findViewById(R.id.txtVoucherStatus);
            voucheramount = (TextView)mView.findViewById(R.id.txtVoucherAmount);
            usedby = (TextView)mView.findViewById(R.id.txtUsedBy);
            useddate = (TextView)mView.findViewById(R.id.txtUsedDate);
            more = (ImageView)mView.findViewById(R.id.img_more);
            mListener= mItemClickListener;
            more.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.userItemClick(getAdapterPosition());
            }
        }
    }

    public interface OnItemClicked {
        void userItemClick(int pos);
    }


}
