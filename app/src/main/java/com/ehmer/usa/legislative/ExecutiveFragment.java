package com.ehmer.usa.legislative;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private TwoOptionsArrayAdapter<Bill> mAdapter;

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
        mAdapter = new TwoOptionsArrayAdapter(getActivity());
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

        bind.list.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }

    private static class TwoOptionsArrayAdapter<T> extends ArrayAdapter<T> {

        private final LayoutInflater inflater;
        private String option1Text;
        private String option2Text;
        private OptionClickListener option1Listener;
        private OptionClickListener option2Listener;

        public TwoOptionsArrayAdapter(Context context) {
            super(context, 0);
            inflater = LayoutInflater.from(context);
        }

        void setOptionOneListener(String label, OptionClickListener<T> listener) {
            option1Text = label;
            option1Listener = listener;
        }

        void setOptionTwoListener(String label, OptionClickListener<T> listener) {
            option2Text = label;
            option2Listener = listener;
        }


        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.list_item_two_options, parent, false);
            } else {
                view = convertView;
            }

            TextView text = (TextView) view.findViewById(R.id.two_options_item_text);
            Button button1 = (Button) view.findViewById(R.id.two_options_item_button_1);
            Button button2 = (Button) view.findViewById(R.id.two_options_item_button_2);

            final T item = getItem(position);
            text.setText(item.toString());

            if (!TextUtils.isEmpty(option1Text) && option1Listener != null) {
                button1.setText(option1Text);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        option1Listener.onOptionClicked(item);
                    }
                });
            } else {
                button1.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(option2Text) && option2Listener != null) {
                button2.setText(option2Text);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        option2Listener.onOptionClicked(item);
                    }
                });
            } else {
                button2.setVisibility(View.GONE);
            }

            return view;


        }

        interface OptionClickListener<T> {
            void onOptionClicked(T item);

        }
    }
}
