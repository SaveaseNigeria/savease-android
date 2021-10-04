package saveaseng.ng.savease.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.Model.TransactionModel;
import saveaseng.ng.savease.R;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    ArrayList<TransactionModel> transactionModels;
    Context context;

    public TransactionAdapter(ArrayList<TransactionModel> transactionModels) {
        this.transactionModels = transactionModels;

    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_transaction_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder viewHolder, int i) {

        TransactionModel model = transactionModels.get(i);


        if (model.getCredit().equalsIgnoreCase("0")){
            viewHolder.credit.setText(model.getDebit());
            viewHolder.credit.setTextColor(Color.parseColor("#FF0000"));
        }else if (model.getDebit().equalsIgnoreCase("0")){
            viewHolder.credit.setText(model.getCredit());
            viewHolder.credit.setTextColor(Color.parseColor("#00FA9A"));

        }else {
            viewHolder.credit.setText("");

        }



        viewHolder.accountNum.setText(model.getAccountNUmber());
        viewHolder.refNum.setText("Ref :"+model.getRefNumber());
        viewHolder.transType.setText(model.getTransType());
        viewHolder.transDate.setText(model.getTransDate());
        viewHolder.name.setText(model.getSenderName());


    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,transDate,transType,refNum,accountNum,credit,debit;
        View mView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            name = (TextView)mView.findViewById(R.id.txtName);
            transDate = (TextView)mView.findViewById(R.id.txtTransactionDate);
            transType = (TextView)mView.findViewById(R.id.txtTransactionType);
            refNum = (TextView)mView.findViewById(R.id.txtRefNumber);
            accountNum = (TextView)mView.findViewById(R.id.txtAccountNo);
            credit = (TextView)mView.findViewById(R.id.txtCredit);

        }
    }
}
