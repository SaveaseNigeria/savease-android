package saveaseng.ng.savease.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import saveaseng.ng.savease.Model.CardModel;
import saveaseng.ng.savease.R;

public class CardAdapter extends ArrayAdapter<CardModel> {

    Context context;
    ArrayList<CardModel> cardModels;

    public CardAdapter(Context context, ArrayList<CardModel> cardModels) {
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
                    R.layout.singlle_voucher_item,parent,false);
        }

        ImageView cardView = convertView.findViewById(R.id.imgCard);
        TextView cardAmount = convertView.findViewById(R.id.imgCardAmount);


        CardModel model = getItem(position);

        if (model != null) {
            cardView.setImageResource(model.getCardId());
            cardAmount.setText(model.getAmount());
        }
        return convertView;
    }
}
