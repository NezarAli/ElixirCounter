package brackets.elixircounter.shared.dialogs

public class MessageDialog {

    private final int ACTION_ASK_PERMISSION = 1, ACTION_CLOSE = 2, ACTION_DISMISS = 3, ACTION_EXIT = 4,
            ACTION_REOPEN = 5, ACTION_UPDATE = 6, ACTION_WATCH_AD = 7

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_message, container)

        if (getArguments() == null) {
            dismiss()
            return view
        }

        Dialog dialog = getDialog()
        TextView textView = view.findViewById(R.id.textView)
        ScrollView scrollView = view.findViewById(R.id.scrollView)
        Button negativeButton = view.findViewById(R.id.negativeButton)
        Button positiveButton = view.findViewById(R.id.positiveButton)
        Button neutralButton = view.findViewById(R.id.neutralButton)

        assert dialog != null
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog)
        }

        negativeButton.setOnClickListener(this)
        positiveButton.setOnClickListener(this)
        neutralButton.setOnClickListener(this)

        assert getArguments() != null
        MessageType messageType = (MessageType) getArguments().getSerializable(FieldName.MESSAGE_TYPE.toString())
        assert messageType != null
        switch (messageType) {
            case ASK_PERMISSION:
                textView.setVisibility(View.GONE)
                scrollView.setVisibility(View.VISIBLE)
                setUpButton(positiveButton, ACTION_ASK_PERMISSION, getString(R.string.ok))
                setUpButton(negativeButton, ACTION_DISMISS, getString(R.string.cancel))
                break
            case ERROR:
                textView.setText(getString(R.string.error))
                setUpButton(neutralButton, ACTION_DISMISS, getString(R.string.ok))
                break
            case EXIT:
                textView.setText(getString(R.string.exit))
                setUpButton(positiveButton, ACTION_EXIT, getString(R.string.yes))
                setUpButton(negativeButton, ACTION_DISMISS, getString(R.string.no))
                break
            case MAINTENANCE:
                textView.setText(getString(R.string.maintenance_break))
                setUpButton(neutralButton, ACTION_CLOSE, getString(R.string.ok))
                break
            case MANDATORY_UPDATE:
                textView.setText(getString(R.string.mandatory_update))
                setUpButton(positiveButton, ACTION_UPDATE, getString(R.string.update_now))
                setUpButton(negativeButton, ACTION_CLOSE, getString(R.string.close_the_app))
                break
            case NO_INTERNET:
                textView.setText(getString(R.string.no_internet))
                setUpButton(neutralButton, ACTION_CLOSE, getString(R.string.ok))
                break
            case OPTIONAL_UPDATE:
                textView.setText(getString(R.string.optional_update))
                setUpButton(positiveButton, ACTION_UPDATE, getString(R.string.update_now))
                setUpButton(negativeButton, ACTION_DISMISS, getString(R.string.update_later))
                break
            case REOPEN:
                textView.setText(getString(R.string.error))
                setUpButton(positiveButton, ACTION_REOPEN, getString(R.string.reopen))
                setUpButton(negativeButton, ACTION_CLOSE, getString(R.string.close))
                break
            case TIP:
                textView.setText(getArguments().getString(FieldName.TEXT.toString()))
                break
            case TOKENS_LIMIT:
                textView.setText(getString(R.string.tokens_limit))
                setUpButton(neutralButton, ACTION_DISMISS, getString(R.string.ok))
                break
            case WATCH_AD:
                textView.setText(getString(R.string.watch_ad))
                setUpButton(positiveButton, ACTION_WATCH_AD, getString(R.string.yes))
                setUpButton(negativeButton, ACTION_DISMISS, getString(R.string.no))
                break
        }

        return view
    }

    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case ACTION_ASK_PERMISSION:
                assert getActivity() != null
                ((HomeActivity) getActivity()).startAskingForPermissions()
                dismiss()
                break
            case ACTION_CLOSE:
                finish()
                break
            case ACTION_DISMISS:
                dismiss()
                break
            case ACTION_EXIT:
                if (OverlayService.isRunning) {
                    assert getActivity() != null
                    ((HomeActivity) getActivity()).stopAndUnbind()
                }

                finish()
                break
            case ACTION_REOPEN:
                finish()
                startActivity(new Intent(getActivity(), MainActivity.class))
                break
            case ACTION_UPDATE:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + Data.getStatics().domain)))
                dismiss()
                finish()
                break
            case ACTION_WATCH_AD:
                assert getActivity() != null
                ((HomeActivity) getActivity()).showAd()
                dismiss()
                break
        }
    }

    private void finish() {
        assert getActivity() != null
        getActivity().finish()
    }

    private void setUpButton(Button button, int action, String text) {
        button.setVisibility(View.VISIBLE)
        button.setTag(action)
        button.setText(text)
    }

    public static boolean isDialogCancelable(MessageType messageType) {
        switch (messageType) {
            case ASK_PERMISSION:
            case ERROR:
            case EXIT:
            case OPTIONAL_UPDATE:
            case TOKENS_LIMIT:
            case WATCH_AD:
                return true
            default:
                return false
        }
    }
}