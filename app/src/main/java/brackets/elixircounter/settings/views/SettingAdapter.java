package brackets.elixircounter.settings.views

public class SettingAdapter {

    List<Option> options

    public SettingAdapter(List<Option> options) {
        this.options = options
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
        Option option = options.get(viewType)

        switch (option.settingType) {
            case SEND_A_FEEDBACK:
            case VERSION:
            case RESET_SETTINGS:
            case ID:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false)
                return new SettingTextHolder(view, option.settingType)
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch, parent, false)
                return new SettingSwitchHolder(view, option)
        }
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Option option = options.get(position)

        switch (option.settingType) {
            case SEND_A_FEEDBACK:
            case VERSION:
            case RESET_SETTINGS:
            case ID:
                ((SettingTextHolder) holder).display(option.text)
                break
            default:
                ((SettingSwitchHolder) holder).display(option.text, option.setting, option.enabled)
                break
        }
    }

    public int getItemCount() {
        return options.size()
    }

    public int getItemViewType(int position) {
        return position
    }
}