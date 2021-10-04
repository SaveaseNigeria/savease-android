package saveaseng.ng.savease.Deposit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import saveaseng.ng.savease.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BeneficiaryDeposit extends Fragment {


    public BeneficiaryDeposit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beneficiary_deposit, container, false);
    }

}
