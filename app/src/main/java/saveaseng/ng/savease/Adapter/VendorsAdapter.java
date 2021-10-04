package saveaseng.ng.savease.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.MainActivity;
import saveaseng.ng.savease.Model.VendorModel;
import saveaseng.ng.savease.R;

public class VendorsAdapter  extends RecyclerView.Adapter<VendorsAdapter.MyViewholder> {

    ArrayList<VendorModel> voucherTableModels;
    Context context;

    public VendorsAdapter(ArrayList<VendorModel> voucherTableModels) {
        this.voucherTableModels = voucherTableModels;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_vendor_item,viewGroup,false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder myViewholder, int i) {
        VendorModel vendorModel = voucherTableModels.get(i);

        myViewholder.callVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "tel:"+vendorModel.getVendorNumber();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(number));

               myViewholder.callVendor.getContext().startActivity(callIntent);
            }
        });
        myViewholder.address.setText(vendorModel.getVendorAddress());
        myViewholder.name.setText(vendorModel.getVendorName());
        myViewholder.status.setText(vendorModel.getStatus());

    }

    @Override
    public int getItemCount() {
        return voucherTableModels.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {
        View mView;
        TextView name,address,status;
        Button callVendor;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            name = (TextView)mView.findViewById(R.id.txtVendorName);
            address = (TextView)mView.findViewById(R.id.txtVendorBizAdd);
            callVendor = (Button) mView.findViewById(R.id.btnCallVendor);
            status = (TextView)mView.findViewById(R.id.txtVendorBizStatus);
        }
    }
}
