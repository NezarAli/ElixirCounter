package brackets.elixircounter.tutorial.screens

public class TutorialFragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false)

        assert getArguments() != null
        int type = getArguments().getInt(FieldName.TYPE.toString(), 1)

        ScrollView scrollView = view.findViewById(R.id.scrollView)
        VideoView videoView = view.findViewById(R.id.videoView)

        switch (type) {
            case TutorialActivity.TYPE_TEXT:
                scrollView.setVisibility(View.VISIBLE)
                break
            case TutorialActivity.TYPE_VIDEO:
                assert getActivity() != null
                videoView.setVisibility(View.VISIBLE)

                MediaController mediaController = new MediaController(getActivity())
                mediaController.setAnchorView(videoView)

                videoView.setMediaController(mediaController)
                videoView.setVideoPath("android.resource://" + getActivity().getPackageName() + "/" + R.raw.tutorial)

                videoView.setOnPreparedListener(mediaPlayer -> {
                    videoView.start()
                    mediaPlayer.setLooping(true)
                })
                break
        }

        return view
    }
}