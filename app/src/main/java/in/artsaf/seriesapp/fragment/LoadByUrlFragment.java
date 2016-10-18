package in.artsaf.seriesapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import in.artsaf.seriesapp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoadByUrlInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoadByUrlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadByUrlFragment extends Fragment implements View.OnClickListener {
    private LoadByUrlInteractionListener mListener;

    public LoadByUrlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoadByUrlFragment.
     */
    public static LoadByUrlFragment newInstance() {
        LoadByUrlFragment fragment = new LoadByUrlFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_load_by_url, container, false);
        Button btnLoad = (Button) view.findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null && getView() != null) {
            EditText txt = (EditText)getView().findViewById(R.id.editTextUrl);
            mListener.onLoadByUrlInteraction(txt.getText().toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoadByUrlInteractionListener) {
            mListener = (LoadByUrlInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoadByUrlInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LoadByUrlInteractionListener {
        void onLoadByUrlInteraction(String url);
    }
}
