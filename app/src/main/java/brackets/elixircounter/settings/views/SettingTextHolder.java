package brackets.elixircounter.settings.views

class SettingTextHolder {

    SettingsActivity settingsActivity
    SettingType settingType

    ConstraintLayout constraintLayout
    TextView textView

    public SettingTextHolder(@NonNull View itemView, SettingType settingType) {
        super(itemView)
        settingsActivity = (SettingsActivity) itemView.getContext()
        this.settingType = settingType

        constraintLayout = itemView.findViewById(R.id.constraintLayout)
        textView = itemView.findViewById(R.id.textView)

        constraintLayout.setOnClickListener(this)
    }

    public void display(String text) {
        textView.setText(text)
    }

    public void onClick(View v) {
        settingsActivity.handleClicks(settingType)
    }
}