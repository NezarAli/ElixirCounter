package brackets.elixircounter.settings.screens

public class SettingsActivity {

    Data data
    Setting setting
    List<Option> options
    SettingAdapter settingAdapter

    RecyclerView recyclerView
    AdView adView

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        data = Data.getDataInstance(this)
        setting = Data.getSettingInstance(this)

        recyclerView = findViewById(R.id.recyclerView)
        adView = findViewById(R.id.adView)

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL))
        adView.loadAd(new AdRequest.Builder().build())

        setUp()
    }

    private void setUp() {
        options = new ArrayList<>()
        options.add(new Option(SettingType.ELIXIR_COUNTER_MODE, getString(R.string.elixir_counter_mode), setting.elixirCounterMode))
        options.add(new Option(SettingType.ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON, getString(R.string.always_show_manual_elixir_button), setting.alwaysShowManualElixirButton))
        options.add(new Option(SettingType.ALLOW_CYCLING_CARDS, getString(R.string.allow_cycling_cards), setting.allowCyclingCards))
        options.add(new Option(SettingType.SUPPORT_DIFFERENT_BATTLE_MODES, getString(R.string.support_different_battle_modes), setting.supportDifferentBattleModes))
        options.add(new Option(SettingType.SHOW_CONFIDENCE, getString(R.string.show_confidence), setting.showConfidence))
        options.add(new Option(SettingType.INVERT_LAYOUT, getString(R.string.invert_layout), setting.invertLayout, setting.elixirCounterMode || setting.alwaysShowManualElixirButton || setting.showConfidence))
        options.add(new Option(SettingType.SEND_A_FEEDBACK, getString(R.string.send_a_feedback)))
        options.add(new Option(SettingType.RESET_SETTINGS, getString(R.string.reset)))
        options.add(new Option(SettingType.VERSION, getString(R.string.version, String.valueOf(Functions.getVersion(this)))))
        options.add(new Option(SettingType.ID, getString(R.string.id, Data.getStatics().id)))

        settingAdapter = new SettingAdapter(options)

        recyclerView.setAdapter(settingAdapter)
        recyclerView.setLayoutManager(new LinearLayoutManager(this))
    }

    public void manualChange(SettingType settingType, boolean flag) {
        switch (settingType) {
            case ELIXIR_COUNTER_MODE:
                autoChange(SettingType.ELIXIR_COUNTER_MODE, flag)
                if (flag) {
                    autoChange(SettingType.ALLOW_CYCLING_CARDS, true)
                } else {
                    autoChange(SettingType.ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON, false)
                    autoChange(SettingType.SUPPORT_DIFFERENT_BATTLE_MODES, false)
                }
                checkInvertOption()
                break
            case ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON:
                autoChange(SettingType.ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON, flag)
                if (flag) {
                    autoChange(SettingType.ELIXIR_COUNTER_MODE, true)
                    autoChange(SettingType.ALLOW_CYCLING_CARDS, true)
                }
                checkInvertOption()
                break
            case ALLOW_CYCLING_CARDS:
                autoChange(SettingType.ALLOW_CYCLING_CARDS, flag)
                if (!flag) {
                    autoChange(SettingType.ELIXIR_COUNTER_MODE, false)
                    autoChange(SettingType.ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON, false)
                    autoChange(SettingType.SUPPORT_DIFFERENT_BATTLE_MODES, false)
                }
                checkInvertOption()
                break
            case SUPPORT_DIFFERENT_BATTLE_MODES:
                autoChange(SettingType.SUPPORT_DIFFERENT_BATTLE_MODES, flag)
                if (flag) {
                    autoChange(SettingType.ELIXIR_COUNTER_MODE, true)
                    autoChange(SettingType.ALLOW_CYCLING_CARDS, true)
                }
                break
            case SHOW_CONFIDENCE:
                autoChange(SettingType.SHOW_CONFIDENCE, flag)
                checkInvertOption()
                break
            case INVERT_LAYOUT:
                autoChange(SettingType.INVERT_LAYOUT, flag)
                break
        }
    }

    private void autoChange(SettingType settingType, boolean flag) {
        if (options.get(getIndex(settingType)).setting == flag) {
            return
        }

        switch (settingType) {
            case ELIXIR_COUNTER_MODE:
                setting.elixirCounterMode = flag
                break
            case ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON:
                setting.alwaysShowManualElixirButton = flag
                break
            case ALLOW_CYCLING_CARDS:
                setting.allowCyclingCards = flag
                break
            case SUPPORT_DIFFERENT_BATTLE_MODES:
                setting.supportDifferentBattleModes = flag
                break
            case SHOW_CONFIDENCE:
                setting.showConfidence = flag
                break
            case INVERT_LAYOUT:
                setting.invertLayout = flag
                break
        }

        data.setSettings(setting)
        options.get(getIndex(settingType)).setting = flag
        settingAdapter.notifyItemChanged(getIndex(settingType))
    }

    private void checkInvertOption() {
        int index = getIndex(SettingType.INVERT_LAYOUT)
        boolean enabled = setting.elixirCounterMode || setting.alwaysShowManualElixirButton || setting.showConfidence

        if (options.get(index).enabled != enabled) {
            if (!enabled) {
                setting.invertLayout = false
                options.get(index).setting = false
            }

            data.setSettings(setting)
            options.get(index).enabled = enabled
            settingAdapter.notifyItemChanged(index)
        }
    }

    private int getIndex(SettingType settingType) {
        switch (settingType) {
            case ELIXIR_COUNTER_MODE:
                return 0
            case ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON:
                return 1
            case ALLOW_CYCLING_CARDS:
                return 2
            case SUPPORT_DIFFERENT_BATTLE_MODES:
                return 3
            case SHOW_CONFIDENCE:
                return 4
            case INVERT_LAYOUT:
                return 5
        }

        return -1
    }

    public void handleClicks(SettingType settingType) {
        switch (settingType) {
            case SEND_A_FEEDBACK:
                Intent intent = new Intent(android.content.Intent.ACTION_SEND)
                intent.setType("plain/text")
                intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {getString(R.string.email)})
                startActivity(Intent.createChooser(intent, getString(R.string.send_email)))
                break
            case RESET_SETTINGS:
                setting = new Setting()
                data.setSettings(setting)
                setUp()
                settingAdapter.notifyDataSetChanged()
                Data.resetSettingInstance()
                break
            case ID:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)
                ClipData clip = ClipData.newPlainText(SettingType.ID.toString(), Data.getStatics().id)
                clipboard.setPrimaryClip(clip)

                Functions.showToast(this, getString(R.string.copied))
                break
        }
    }

    public void showTip(SettingType settingType) {
        String text = null

        switch (settingType) {
            case ELIXIR_COUNTER_MODE:
                text = getString(R.string.elixir_counter_mode_tip)
                break
            case ALWAYS_SHOW_MANUAL_ELIXIR_BUTTON:
                text = getString(R.string.always_show_manual_elixir_button_tip)
                break
            case ALLOW_CYCLING_CARDS:
                text = getString(R.string.allow_cycling_cards_tip)
                break
            case SUPPORT_DIFFERENT_BATTLE_MODES:
                text = getString(R.string.support_different_battle_modes_tip)
                break
            case SHOW_CONFIDENCE:
                text = getString(R.string.show_confidence_tip)
                break
            case INVERT_LAYOUT:
                text = getString(R.string.invert_layout_tip)
                break
        }

        Functions.showDialog(this, text)
    }
}