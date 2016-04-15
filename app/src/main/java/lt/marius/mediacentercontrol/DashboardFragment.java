package lt.marius.mediacentercontrol;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.marius.mediacentercontrol.player.requests.ActionResp;
import lt.marius.mediacentercontrol.player.MusicPlayer;
import lt.marius.mediacentercontrol.player.requests.VolumeResp;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    public static final String TYPE_MUSIC = "music";
    public static final String TYPE_RADIO = "radio";


    private static final String ARG_TYPE = "arg_type";

    private String mType = TYPE_RADIO;

    private OnFragmentInteractionListener mListener;


    public static DashboardFragment newInstance(String type) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
        }
    }

    @Bind(R.id.button_play_pause)
    ImageButton playButton;
    @Bind(R.id.button_next)
    ImageButton nextButton;
    @Bind(R.id.button_prev)
    ImageButton prevButton;

    @Bind(R.id.text_playing_type) TextView textPlayingType;
    @Bind(R.id.tv_volume_percent) TextView textVolumePercent;
    @Bind(R.id.sb_volume)
    SeekBar volumeSeekBar;

    private MusicPlayer player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);

        ButterKnife.bind(this, vg);

        MusicPlayer.getInstance().setVolume(-1, new Callback<VolumeResp>() {
            @Override
            public void onResponse(Response<VolumeResp> response, Retrofit retrofit) {
                volumeSeekBar.setProgress(response.body().volume);
                textVolumePercent.setText(response.body().volume + "%");
                volumeSeekBar.setOnSeekBarChangeListener(onSeekChangeListener);
            }

            @Override
            public void onFailure(Throwable t) {
                volumeSeekBar.setOnSeekBarChangeListener(onSeekChangeListener);
                volumeSeekBar.setProgress(50);
                textVolumePercent.setText("50%");
            }
        });

        playButton.setOnClickListener(playClick);
        prevButton.setOnClickListener(prevClick);
        nextButton.setOnClickListener(nextClick);

        TextView tv = (TextView) vg.findViewById(R.id.text_playing_type);
        tv.setText(mType);

        player = MusicPlayer.getInstance();

        return vg;
    }

    private SeekBar.OnSeekBarChangeListener onSeekChangeListener = new SeekBar.OnSeekBarChangeListener() {

        private boolean requestRunning;
        private int lastUpdatedProgress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!requestRunning) {
                requestRunning = true;
                updateVolume(progress);
            }
        }

        private void updateVolume(int progress) {
            MusicPlayer.getInstance().setVolume(progress, new Callback<VolumeResp>() {
                @Override
                public void onResponse(Response<VolumeResp> response, Retrofit retrofit) {
                    requestRunning = false;
                    lastUpdatedProgress = response.body().volume;
                    textVolumePercent.setText(response.body().volume + "%");
                }

                @Override
                public void onFailure(Throwable t) {
                    requestRunning = false;
                }
            });
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (lastUpdatedProgress != seekBar.getProgress()) {
                updateVolume(seekBar.getProgress());
            }
        }
    };

    private View.OnClickListener playClick = new View.OnClickListener() {

        private String[] folders = {"", "Queen", "Kygo", "Avicii/Stories"};
        private int playing;

        @Override
        public void onClick(View v) {
            if (mType.equals(TYPE_MUSIC)) {
                player.play(folders[playing], updateUiCallback);
                playing = (playing + 1) % folders.length;
            } else {
                player.radio(updateUiCallback);

            }
        }
    };

    private View.OnClickListener pauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            player.pause(updateUiCallback);
        }
    };

    private View.OnClickListener prevClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            player.prev(updateUiCallback);
        }
    };

    private View.OnClickListener nextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            player.next(updateUiCallback);
        }
    };

    private Callback<ActionResp> updateUiCallback = new Callback<ActionResp>() {
        @Override
        public void onResponse(Response<ActionResp> response, Retrofit retrofit) {
            updateUI(response.body());
        }

        @Override
        public void onFailure(Throwable t) {
            //render ui error
        }
    };

    private void updateUI(ActionResp response) {
        switch (response.status) {
            case ActionResp.STATUS_PAUSED:
                playButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_play));
                playButton.setOnClickListener(playClick);
                break;
            case ActionResp.STATUS_PLAYING:
                playButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_pause));
                playButton.setOnClickListener(pauseClick);
                break;
            case ActionResp.STATUS_STOPPED:
                playButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_play));
                playButton.setOnClickListener(playClick);
                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            mListener = null;
        }
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
