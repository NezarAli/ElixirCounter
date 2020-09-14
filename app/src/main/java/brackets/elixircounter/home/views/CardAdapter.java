package brackets.elixircounter.home.views

public class CardAdapter {

    List<Card> cards
    Values values
    RelativeLayout.LayoutParams cardParams
    ColorMatrixColorFilter coloredFilter, uncoloredFilter

    boolean elixirCounterMode, allowCyclingCards

    public CardAdapter(Context context, List<Card> cards, boolean elixirCounterMode, boolean allowCyclingCards) {
        this.cards = cards
        this.elixirCounterMode = elixirCounterMode
        this.allowCyclingCards = allowCyclingCards

        values = Data.getDataInstance(context).getDimensions().cards
        cardParams = new RelativeLayout.LayoutParams(values.width, values.height)
        coloredFilter = new ColorMatrixColorFilter(getColorMatrix(1))
        uncoloredFilter = new ColorMatrixColorFilter(getColorMatrix(0))
    }

    @NonNull
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false)
        return new CardHolder(view, allowCyclingCards)
    }

    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        boolean flag = true

        if (elixirCounterMode) {
            flag = cards.get(position).available
        } else if (allowCyclingCards) {
            flag = cards.get(position).elixir != 0
        }

        holder.display(cards.get(position), cardParams, flag ? coloredFilter : uncoloredFilter)
    }

    public int getItemCount() {
        return cards.size()
    }

    private ColorMatrix getColorMatrix(int saturation) {
        ColorMatrix colorMatrix = new ColorMatrix()
        colorMatrix.setSaturation(saturation)
        return colorMatrix
    }
}