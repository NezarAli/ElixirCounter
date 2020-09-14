package brackets.elixircounter.home.views

public class CardHolder {

    ImageView mirroredImage, image

    public CardHolder(@NonNull View itemView, boolean allowCyclingCards) {
        super(itemView)
        mirroredImage = itemView.findViewById(R.id.mirroredImage)
        image = itemView.findViewById(R.id.image)

        if (allowCyclingCards) {
            image.setOnClickListener(this)
        }
    }

    public void display(Card card, RelativeLayout.LayoutParams cardParams, ColorMatrixColorFilter colorMatrixColorFilter) {
        image.setLayoutParams(cardParams)

        if (card.mirroredImage != null) {
            Glide.with(mirroredImage.getContext()).load(card.mirroredImage).into(mirroredImage)
            mirroredImage.setColorFilter(colorMatrixColorFilter)
            mirroredImage.setLayoutParams(cardParams)
        }

        Glide.with(image.getContext()).load(card.image).placeholder(R.drawable.icon_card).into(image)

        image.setColorFilter(colorMatrixColorFilter)
        image.setTag(card.id)
    }

    public void onClick(View v) {
        ((OverlayService) v.getContext()).removeCard(Integer.parseInt(v.getTag().toString()))
    }
}