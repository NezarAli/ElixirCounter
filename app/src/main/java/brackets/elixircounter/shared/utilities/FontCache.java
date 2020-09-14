package brackets.elixircounter.shared.utilities

public class FontCache {

    private static Typeface typeface

    public static Typeface getTypeface(AssetManager assetManager) {
        return typeface != null ? typeface : generateTypeface(assetManager)
    }

    private static Typeface generateTypeface(AssetManager assetManager) {
        try {
            typeface = Typeface.createFromAsset(assetManager, "fonts/Supercell-Magic.ttf")
            return typeface
        } catch (Exception e) {
            return null
        }
    }
}