package fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bramgussekloo.projectb.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationsAdminFragment extends Fragment {


    public ReservationsAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservations_admin, container, false);
    }

}
