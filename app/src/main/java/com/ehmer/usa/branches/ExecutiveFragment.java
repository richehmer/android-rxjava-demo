package com.ehmer.usa.branches;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ehmer.usa.R;
import com.ehmer.usa.UsaApplication;
import com.ehmer.usa.bill.Bill;
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

    private TwoOptionsArrayAdapter<Bill> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UsaApplication.get(getActivity()).component().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_executive, container, false);
        setupViews(inflate);
        return inflate;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscriptions.add(messageService.proposedBills().subscribe(new Action1<List<Bill>>() {
            @Override
            public void call(List<Bill> bills) {
                //populate list of bills
                mAdapter.clear();
                mAdapter.addAll(bills);
            }
        }));

    }

    void setupViews(View root) {
        mAdapter = new TwoOptionsArrayAdapter<>(getActivity());
        mAdapter.setOptionOneListener(getString(R.string.veto),
                new TwoOptionsArrayAdapter.OptionClickListener<Bill>() {
                    @Override
                    public void onOptionClicked(Bill item) {
                        messageService.vetoBill(item);
                        Toast.makeText(getContext(), "Vetoed " + item.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        mAdapter.setOptionTwoListener(getString(R.string.sign),
                new TwoOptionsArrayAdapter.OptionClickListener<Bill>() {
                    @Override
                    public void onOptionClicked(Bill item) {
                        messageService.signBill(item);
                        Toast.makeText(getContext(), "Passed " + item.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        ListView listView = (ListView) root.findViewById(R.id.list);
        View emptyView = root.findViewById(R.id.empty);
        listView.setEmptyView(emptyView);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }

}
