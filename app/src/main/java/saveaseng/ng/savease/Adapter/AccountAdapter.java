package saveaseng.ng.savease.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Activities.BankActivity;
import saveaseng.ng.savease.Model.AccountModel;
import saveaseng.ng.savease.R;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    private static final String METHOD_NAME = "deletebankdetials";
    private static final String SOAP_ACTION = "http://savease.ng/deletebankdetials";

    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";

    ArrayList<AccountModel> transactionModels;
    Context context;

    public AccountAdapter(ArrayList<AccountModel> transactionModels) {
        this.transactionModels = transactionModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_account_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        AccountModel notificationModel = transactionModels.get(i);

        viewHolder.transDate.setText(notificationModel.getBankName());
        viewHolder.message.setText(notificationModel.getAccountNumber());
        viewHolder.transType.setText(notificationModel.getAccountName());
        viewHolder.img_delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")


                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                new updateAccount().execute(notificationModel.getAccountNumber());
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


    }

    private class updateAccount extends AsyncTask<String, String, String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Removing Details ,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.pDialog.dismiss();
            if (s.equalsIgnoreCase("1")) {
                pDialog.dismiss();
                Toast.makeText(context, "Successfully deleted account details", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context,BankActivity.class));

            } else {

                pDialog.dismiss();
                Toast.makeText(context, "Successfully deleted account details", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String name = strings[0];




            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("accountNumber", name);



            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object result = null;
            try {
                result = (Object) envelope.getResponse();
                Log.i("RESPONSE", String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return String.valueOf(result);

        }
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mview;
        TextView transType,message,transDate;
        ImageButton img_delete_acc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;

            transType = (TextView)mview.findViewById(R.id.txtAccountNameBank);
            message = (TextView)mview.findViewById(R.id.txtAccountNumberBank);
            transDate = (TextView)mview.findViewById(R.id.txtBankNameBank);
            img_delete_acc = (ImageButton)mview.findViewById(R.id.img_delete_acc);
        }
    }
}
