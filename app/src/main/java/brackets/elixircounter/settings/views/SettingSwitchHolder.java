package brackets.elixircounter.settings.views

class SettingSwitchHolder {

    SettingsActivity settingsActivity
    Option option

    ConstraintLayout constraintLayout
    TextView textView
    Button button
    SwitchCompat switchCompat

    public SettingSwitchHolder(@NonNull View itemView, Option option) {
        super(itemView)
        settingsActivity = (SettingsActivity) itemView.getContext()
        this.option = option

        constraintLayout = itemView.findViewById(R.id.constraintLayout)
        textView = itemView.findViewById(R.id.textView)
        button = itemView.findViewById(R.id.button)
        switchCompat = itemView.findViewById(R.id.switchCompat)

        constraintLayout.setOnClickListener(this)
        button.setOnClickListener(this)
        switchCompat.setOnCheckedChangeListener(this)

        Functions.increaseHitArea(button)
    }

    public void display(String text, boolean setting, boolean enabled) {
        textView.setText(text)
        switchCompat.setChecked(setting)
        switchCompat.setEnabled(enabled)
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.constraintLayout:
                if (option.enabled) {
                    settingsActivity.manualChange(option.settingType, !option.setting)
                }
                break
            case R.id.button:
                settingsActivity.showTip(option.settingType)
                break
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked != option.setting) {
            settingsActivity.manualChange(option.settingType, isChecked)
        }
    }
}