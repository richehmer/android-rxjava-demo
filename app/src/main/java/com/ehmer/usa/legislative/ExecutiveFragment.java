package com.ehmer.usa.legislative;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ehmer.usa.R;
import com.ehmer.usa.UsaApplication;
import com.ehmer.usa.bill.Bill;
import com.ehmer.usa.databinding.FragmentExecutiveBinding;
import com.ehmer.usa.messaging.ConstitutionalMessageService;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Ehmer, R.G. on 12/8/16.
 */

public class ExecutiveFragment extends Fragment {

    @Inject
    ConstitutionalMessageService messageService;

    final CompositeSubscription subscriptions = new CompositeSubscription();

    FragmentExecutiveBinding bind;
    private ArrayAdapter<Bill> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UsaApplication.get(getActivity()).component().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_executive, container, false);
        return bind.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        subscriptions.add(messageService.proposedBills().subscribe(new Action1<List<Bill>>() {
            @Override
            public void call(List<Bill> bills) {
                //populate list of bills
                mAdapter.clear();
                mAdapter.addAll(bills);
            }
        }));

    }

    void setupViews() {
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        bind.list.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }
}
