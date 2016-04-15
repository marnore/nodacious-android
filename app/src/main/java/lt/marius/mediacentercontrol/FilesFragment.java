package lt.marius.mediacentercontrol;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.marius.mediacentercontrol.player.FolderListing;
import lt.marius.mediacentercontrol.player.MediaInfo;
import lt.marius.mediacentercontrol.player.MusicPlayer;
import lt.marius.mediacentercontrol.player.requests.SongsResp;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INITIAL_DIR = "arg_initial_dir";
    private static final String ARG_PARAM2 = "param2";

    private String mInitialDir;


    public FilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilesFragment.
     */
    public static FilesFragment newInstance(String initialDir) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INITIAL_DIR, initialDir);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInitialDir = getArguments().getString(ARG_INITIAL_DIR);
        }
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TextView mErrorTextView;

    private FilesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_files, container, false);

        mErrorTextView = (TextView) vg.findViewById(R.id.tv_no_data);

        mRecyclerView = (RecyclerView) vg.findViewById(R.id.rv_folders);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        listAll(mInitialDir);

        return vg;
    }

    private void listAll(final String root) {
        final MusicPlayer player = MusicPlayer.getInstance();
        player.getFolders(root, new Callback<FolderListing>() {
            @Override
            public void onResponse(Response<FolderListing> response, Retrofit retrofit) {
                // specify an adapter (see also next example)
                List<FileInfo> list = new ArrayList<>();
                if (response.body().folders != null) {
                    for (String folderName : response.body().folders) {
                        list.add(new FileInfo(folderName, true, null));
                    }
                }
                mAdapter = new FilesAdapter(list,
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_folder_open_black_36dp),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_library_music_black_36dp),
                        itemClickListener);
                mRecyclerView.setAdapter(mAdapter);

                player.getSongs(root, new Callback<SongsResp>() {
                    @Override
                    public void onResponse(Response<SongsResp> response, Retrofit retrofit) {
                        List<FileInfo> list = mAdapter.getDataset();
                        if (response.body().getSongs() != null) {
                            for (MediaInfo info : response.body().getSongs()) {
                                list.add(new FileInfo(info.fileName, false, info));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                mRecyclerView.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private FilesAdapterActionListener itemClickListener = new FilesAdapterActionListener() {
        @Override
        public void onClick(FileInfo fileInfo) {
            if (fileInfo.isDirectory) {
                File path = new File(mInitialDir, fileInfo.name);
                mInitialDir = path.getPath();
                listAll(mInitialDir);
            }
        }

        @Override
        public void onFileAdded(FileInfo fileInfo) {

        }

        @Override
        public void onFileRemoved(FileInfo fileInfo) {

        }
    };

    static class FileInfo {
        public final String name;
        public final boolean isDirectory;
        public final MediaInfo songInfo;

        FileInfo(String name, boolean isDirectory, MediaInfo songInfo) {
            this.name = name;
            this.isDirectory = isDirectory;
            this.songInfo = songInfo;
        }
    }

    public interface FilesAdapterActionListener {
        void onClick(FileInfo fileInfo);
        void onFileAdded(FileInfo fileInfo);
        void onFileRemoved(FileInfo fileInfo);
    }

    public static class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {
        private List<FileInfo> mDataset;
        private Drawable mFolderIcon, mFileIcon;
        private FilesAdapterActionListener listener;


        public List<FileInfo> getDataset() {

            return mDataset;
        }

        // Provide a reference to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            @Bind(R.id.tv_title)
            public TextView titleView;
            @Bind(R.id.tv_subtitle)
            public TextView subtitleView;
            @Bind(R.id.iv_icon)
            public ImageView icon;
            @Bind(R.id.checkbox)
            public CheckBox checkBox;

            public ViewHolder(ViewGroup v) {
                super(v);
                ButterKnife.bind(this, v);
            }
        }

        public FilesAdapter(List<FileInfo> files, Drawable folderIcon, Drawable fileIcon,
                            FilesAdapterActionListener l) {
            mDataset = files;
            mFileIcon = fileIcon;
            mFolderIcon = folderIcon;
            listener = l;
        }

        @Override
        public FilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_file_info, parent, false);
            // set the view's size, margins, paddings and layout parameters??
            return new ViewHolder((ViewGroup) v);
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    int position = (int) v.getTag();
                    if (listener != null) {
                        listener.onClick(mDataset.get(position));
                    }
                }
            }
        };

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final FileInfo fileInfo = mDataset.get(position);
            holder.titleView.setText(fileInfo.name);
            if (fileInfo.songInfo != null) {
                holder.subtitleView.setText(fileInfo.songInfo.artist + " - " + fileInfo.songInfo.title);
            }
            if (fileInfo.isDirectory) {
                holder.icon.setImageDrawable(mFolderIcon);
            } else {
                holder.icon.setImageDrawable(mFileIcon);
            }

            if (listener != null) {
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            listener.onFileAdded(fileInfo);
                        } else {
                            listener.onFileRemoved(fileInfo);
                        }
                    }
                });
            }

            holder.titleView.setTag(position);
            holder.subtitleView.setTag(position);
            holder.icon.setTag(position);
            holder.titleView.setOnClickListener(clickListener);
            holder.subtitleView.setOnClickListener(clickListener);
            holder.icon.setOnClickListener(clickListener);

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
