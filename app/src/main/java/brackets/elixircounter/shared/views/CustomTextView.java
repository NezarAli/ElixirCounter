package brackets.elixircounter.shared.views

public class CustomTextView {

    public CustomTextView(Context context) {
        super(context)
        applyTypeface(context)
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr)
        applyTypeface(context)
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs)
        applyTypeface(context)
    }

    public void applyTypeface(Context context) {
        setTypeface(FontCache.getTypeface(context.getAssets()))
    }
}