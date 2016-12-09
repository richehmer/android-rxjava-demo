package com.ehmer.usa.branches;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ehmer.usa.R;

/**
 * Created by Ehmer, R.G. on 12/9/16.
 */
class TwoOptionsArrayAdapter<T> extends ArrayAdapter<T> {

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
