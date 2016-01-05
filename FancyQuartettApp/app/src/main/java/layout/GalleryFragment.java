package layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.GalleryViewAdapter;
import de.uulm.mal.fancyquartett.data.GalleryModel;
import de.uulm.mal.fancyquartett.data.Image;
import de.uulm.mal.fancyquartett.data.Settings;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {

    GalleryViewAdapter galleryViewAdapter;
    RecyclerView recList;
    LinearLayoutManager llm;
    Activity parentActivity;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        //Create a GallerViewAdapter for the RecyclerList
        galleryViewAdapter = new GalleryViewAdapter(this.getContext(),new GalleryModel(Settings.serverAdress,Settings.localFolder));
        recList = (RecyclerView) rootView.findViewById(R.id.recycler_gallery_list);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setAdapter(galleryViewAdapter);
        //ListButton


        return rootView;
    }

    private void setLayoutButtonToToolbar() {
        parentActivity=getActivity();
        ImageView imageView = (ImageView) parentActivity.findViewById(R.id.listLayoutButton);
        imageView.setRotation(90);
        imageView.setTag("module");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                if (imageView.getTag().equals("module")) {
                    imageView.setImageResource(R.drawable.ic_view_list_24dp);
                    imageView.setRotation(0);
                    imageView.setTag("list");
                } else {
                    imageView.setImageResource(R.drawable.ic_view_module_24dp);
                    imageView.setRotation(90);
                    imageView.setTag("module");
                }
            }
        });
        imageView.setVisibility(View.VISIBLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setLayoutButtonToToolbar();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLayoutButtonToToolbar();
        mListener = null;
    }

    private void hideLayoutButtonToToolbar() {
        parentActivity=getActivity();
        ImageView imageView = (ImageView) parentActivity.findViewById(R.id.listLayoutButton);
        imageView.setVisibility(View.INVISIBLE);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
