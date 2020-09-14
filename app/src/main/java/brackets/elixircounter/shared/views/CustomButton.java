package brackets.elixircounter.shared.views

public class CustomButton {

    public CustomButton(Context context) {
        super(context)
        applyTypeface(context)
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs)
        applyTypeface(context)
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr)
        applyTypeface(context)
    }

    public void applyTypeface(Context context) {
        setTypeface(FontCache.getTypeface(context.getAssets()))
    }
}