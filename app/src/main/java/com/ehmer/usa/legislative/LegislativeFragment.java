package com.ehmer.usa.legislative;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ehmer.usa.R;
import com.ehmer.usa.UsaApplication;
import com.ehmer.usa.bill.Bill;
import com.ehmer.usa.databinding.FragmentLegislativeBinding;
import com.ehmer.usa.messaging.ConstitutionalMessageService;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Ehmer, R.G. on 12/8/16.
 */

public class LegislativeFragment extends Fragment {

    @Inject
    ConstitutionalMessageService messageService;

    final CompositeSubscription subscriptions = new CompositeSubscription();

    FragmentLegislativeBinding bind;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UsaApplication.get(getActivity()).component().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_legislative, container, false);
        return bind.getRoot();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        subscriptions.add(messageService.vetoedBills().subscribe(new Action1<Bill>() {
            @Override
            public void call(Bill bill) {

            }
        }));

    }

    void setupViews() {
        bind.proposeLaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bill bill = new Bill();
                messageService.proposeBill(bill);
                Toast.makeText(getActivity(),"Proposed Bill: "+bill.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }
}
