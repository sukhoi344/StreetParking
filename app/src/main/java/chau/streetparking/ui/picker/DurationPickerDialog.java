package chau.streetparking.ui.picker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import chau.streetparking.R;
import chau.streetparking.util.ImageUtil;

/**
 * Created by Chau Thai on 9/3/15.
 */
public class DurationPickerDialog extends DialogFragment {
    private static final String TAG = DurationPickerDialog.class.getSimpleName();

    public static abstract class DurationType {
        public static final int MINUTE = 0;
        public static final int HOUR = 1;
        public static final int DAY = 2;
        public static final int MONTH = 3;
    }

    private static final int HEIGHT_DP = 400;
    private static final int WIDTH_DP = 250;

    private static final String KEY_DURATION = "duration";
    private static final String KEY_TYPE = "type";

    private RecyclerView    recyclerViewValue;
    private RecyclerView    recyclerViewType;
    private Button          btnOk;
    private Button          btnCancel;

    private int duration;
    private int durationType;

    private boolean loadingValues = true;

    private OnDurationSetListener durationSetListener;

    public static DurationPickerDialog newInstance(int duration, int durationType) {
        DurationPickerDialog dialog = new DurationPickerDialog();

        Bundle args = new Bundle();
        args.putInt(KEY_DURATION, duration);
        args.putInt(KEY_DURATION, durationType);

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        duration = getArguments().getInt(KEY_DURATION);
        durationType = getArguments().getInt(KEY_TYPE);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupSize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.duration_picker, container, false);
        getWidgets(v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupList();
        setupButtons();
    }

    private void setupSize() {
        int height = ImageUtil.getPixelFromDP(HEIGHT_DP);
        int width = ImageUtil.getPixelFromDP(WIDTH_DP);

        getDialog().getWindow().setLayout(width, height);
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setupList() {
        final LinearLayoutManager layoutManagerType = new LinearLayoutManager(getActivity());
        final LinearLayoutManager layoutManagerValue = new LinearLayoutManager(getActivity());

        recyclerViewType.setLayoutManager(layoutManagerType);
        recyclerViewValue.setLayoutManager(layoutManagerValue);
        recyclerViewType.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerViewValue.setOverScrollMode(View.OVER_SCROLL_NEVER);

        final DurationValueAdapter durationValueAdapter = new DurationValueAdapter(getActivity());
        recyclerViewValue.setAdapter(durationValueAdapter);
        recyclerViewValue.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = layoutManagerValue.getChildCount();
                int totalItemCount = layoutManagerValue.getItemCount();
                int pastVisiblesItems = layoutManagerValue.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loadingValues = false;
                    durationValueAdapter.addData();
                    durationValueAdapter.notifyDataSetChanged();
                }
            }
        });

        final DurationTypeAdapter durationTypeAdapter = new DurationTypeAdapter(getActivity());
        recyclerViewType.setAdapter(durationTypeAdapter);
    }

    private void getWidgets(View view) {
        recyclerViewValue = (RecyclerView) view.findViewById(R.id.recycler_view_duration_value);
        recyclerViewType = (RecyclerView) view.findViewById(R.id.recycler_view_duration_type);
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
    }

    public interface OnDurationSetListener {
        void onDurationSet(int duration, int durationType);
    }
}
