package saveaseng.ng.savease.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.Model.AccountModel;
import saveaseng.ng.savease.R;

public class AcAdapter extends ArrayAdapter<AccountModel> {

    Context context;
    ArrayList<AccountModel> cardModels;

    public AcAdapter(Context context, ArrayList<AccountModel> cardModels) {
        super(context,0,cardModels);
        this.context = context;
        this.cardModels = cardModels;
    }


    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @NonNull View convertView,@NonNull  ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position, View convertView,  ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.singlle_accont_item,parent,false);
        }


        TextView cardAmount = convertView.findViewById(R.id.txtAccountNumberDrop);
        TextView cardName = convertView.findViewById(R.id.txtBankNameDrop);


        AccountModel model = getItem(position);

        if (model != null) {
            cardAmount.setText(model.getAccountNumber());
            cardName.setText(model.getBankName());

        }
        return convertView;
    }


}
