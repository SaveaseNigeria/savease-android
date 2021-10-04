package saveaseng.ng.savease.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.Model.NotificationModel;
import saveaseng.ng.savease.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    ArrayList<NotificationModel> transactionModels;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> transactionModels) {
        this.transactionModels = transactionModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_notification_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        NotificationModel notificationModel = transactionModels.get(i);

        viewHolder.transDate.setText(notificationModel.getTransDate());
        viewHolder.message.setText(notificationModel.getMessage());
        viewHolder.transType.setText(notificationModel.getTransType());
        viewHolder.transRef.setText(notificationModel.getTransRef());

    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mview;
        TextView transRef,transType,message,transDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;
            transRef = (TextView)mview.findViewById(R.id.txtTransactionRef);
            transType = (TextView)mview.findViewById(R.id.txtTransactionType);
            message = (TextView)mview.findViewById(R.id.txtMessage);
            transDate = (TextView)mview.findViewById(R.id.txtTransactionDate);
        }
    }
}
