package saveaseng.ng.savease.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.Model.BankListData;
import saveaseng.ng.savease.R;

public class AllBanksAdapter extends RecyclerView.Adapter<AllBanksAdapter.ViewHolder> {

    ArrayList<BankListData> bankListData;
    private OnItemClicked mItemClickListener;

    Context context;

    public AllBanksAdapter(ArrayList<BankListData> bankListData,OnItemClicked mItemClickListener ) {
        this.bankListData = bankListData;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_bank_layout,viewGroup,false);
        return new ViewHolder(view,mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        BankListData bankList= bankListData.get(i);

        viewHolder.bankName.setText(bankList.getName());


    }

    @Override
    public int getItemCount() {
        return bankListData.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        TextView bankName;
        OnItemClicked mListener;
        public ViewHolder(@NonNull View itemView, OnItemClicked mItemClickListener) {
            super(itemView);

            mView = itemView;
            bankName = (TextView)mView.findViewById(R.id.txtBankName);
            mListener= mItemClickListener;
            itemView.setOnClickListener(this);


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
