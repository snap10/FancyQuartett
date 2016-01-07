package layout;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.GalleryViewAdapter;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;

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
    GridLayoutManager glm;
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

        //Initialize Data
        galleryViewAdapter = new GalleryViewAdapter(getContext());
        LocalDecksLoader loader = new LocalDecksLoader(Settings.localAssets,getContext(),galleryViewAdapter);
        loader.execute();

        glm= new GridLayoutManager(getContext(),2);
        llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //ListButton
        setLayoutButtonClickListener();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        //Create a GallerViewAdapter for the RecyclerList

        recList = (RecyclerView) rootView.findViewById(R.id.recycler_gallery_list);
        recList.setHasFixedSize(true);

        recList.setLayoutManager(llm);
        recList.setAdapter(galleryViewAdapter);


        return rootView;
    }

    private void setLayoutButtonClickListener() {
        parentActivity=getActivity();
        ImageView imageView = (ImageView) parentActivity.findViewById(R.id.listLayoutButton);
        if (imageView!=null){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView) v;
                    if (imageView.getTag().equals("module")) {
                        recList.setLayoutManager(llm);
                        galleryViewAdapter = new GalleryViewAdapter(getContext(),galleryViewAdapter.getGalleryModel(),GalleryViewAdapter.LISTLAYOUT);
                        recList.setAdapter(galleryViewAdapter);
                        imageView.setImageResource(R.drawable.ic_view_module_24dp);
                        imageView.setRotation(90);
                        imageView.setTag("list");

                    } else {
                        recList.setLayoutManager(glm);

                        galleryViewAdapter = new GalleryViewAdapter(getContext(),galleryViewAdapter.getGalleryModel(),GalleryViewAdapter.GRIDLAYOUT);
                        recList.setAdapter(galleryViewAdapter);
                        imageView.setImageResource(R.drawable.ic_view_list_24dp);
                        imageView.setRotation(0);
                        imageView.setTag("module");

                    }
                }
            });
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
