package brackets.elixircounter.home.managers

public class ViewsManager {

    OverlayService overlayService
    Data data
    Setting setting
    Dimensions dimensions
    Values cards
    CardAdapter cardAdapter

    int type, flags, margin
    boolean viewsFlag
    private final int WRAP_CONTENT, MATCH_PARENT

    WindowManager windowManager
    WindowManager.LayoutParams buttonsParams, stopParams, retryParams, progressBarParams, cardsParams, textsParams, addParams
    View buttonsView, stopView, retryView, progressBarView, cardsView, textsView, addView
    ConstraintLayout modeButtonLayout, infoButtonLayout
    Button openButton, startButton, modeButton, stopButton, retryButton, addButton
    IndeterminateCenteredRoundCornerProgressBar progressBar
    RecyclerView recyclerView
    TextView elixirText, confidenceText

    public ViewsManager(OverlayService overlayService) {
        this.overlayService = overlayService
        data = Data.getDataInstance(overlayService)
        setting = Data.getSettingInstance(overlayService)
        dimensions = data.getDimensions()
        cards = dimensions.cards

        margin = Functions.dpToPixels(overlayService, 3)
        viewsFlag = true
        WRAP_CONTENT = WindowManager.LayoutParams.WRAP_CONTENT
        MATCH_PARENT = WindowManager.LayoutParams.MATCH_PARENT
    }

    @SuppressLint("InflateParams")
    public void setupViews() {
        windowManager = (WindowManager) overlayService.getSystemService(WINDOW_SERVICE)
        buttonsView = LayoutInflater.from(overlayService).inflate(R.layout.view_buttons, null)
        stopView = LayoutInflater.from(overlayService).inflate(R.layout.view_stop, null)
        retryView = LayoutInflater.from(overlayService).inflate(R.layout.view_retry, null)
        progressBarView = LayoutInflater.from(overlayService).inflate(R.layout.view_progress_bar, null)
        cardsView = LayoutInflater.from(overlayService).inflate(R.layout.view_cards, null)
        textsView = LayoutInflater.from(overlayService).inflate(R.layout.view_texts, null)
        addView = LayoutInflater.from(overlayService).inflate(R.layout.view_add, null)

        modeButtonLayout = buttonsView.findViewById(R.id.modeButtonLayout)
        infoButtonLayout = buttonsView.findViewById(R.id.infoButtonLayout)
        openButton = buttonsView.findViewById(R.id.openButton)
        startButton = buttonsView.findViewById(R.id.startButton)
        modeButton = buttonsView.findViewById(R.id.modeButton)
        stopButton = stopView.findViewById(R.id.stopButton)
        retryButton = retryView.findViewById(R.id.retryButton)
        progressBar = progressBarView.findViewById(R.id.progressBar)
        recyclerView = cardsView.findViewById(R.id.recyclerView)
        elixirText = textsView.findViewById(R.id.elixirText)
        confidenceText = textsView.findViewById(R.id.confidenceText)
        addButton = addView.findViewById(R.id.addButton)

        openButton.setOnClickListener(overlayService)
        startButton.setOnClickListener(overlayService)
        retryButton.setOnClickListener(overlayService)
        modeButton.setOnClickListener(overlayService)
        infoButtonLayout.setOnClickListener(overlayService)
        stopButton.setOnClickListener(overlayService)
        recyclerView.setItemAnimator(null)
        addButton.setOnClickListener(overlayService)

        type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams.FLAG_FULLSCREEN

        buttonsParams = getParams(MATCH_PARENT, dimensions.display.buttonHeight)
        progressBarParams = getParams(MATCH_PARENT, dimensions.display.progressBarHeight)
        stopParams = getParams(WRAP_CONTENT, WRAP_CONTENT)
        retryParams = getParams(WRAP_CONTENT, WRAP_CONTENT)
        cardsParams = getParams(MATCH_PARENT, WRAP_CONTENT)

        buttonsParams.gravity = Gravity.BOTTOM
        stopParams.gravity = Gravity.BOTTOM|(!setting.invertLayout ? Gravity.END : Gravity.START)
        retryParams.gravity = Gravity.BOTTOM|(!setting.invertLayout ? Gravity.END : Gravity.START)
        progressBarParams.gravity = Gravity.BOTTOM

        stopParams.x = margin
        stopParams.y = margin
        retryParams.y = margin

        tryShowingButtons(true)

        if (!setting.supportDifferentBattleModes) {
            modeButtonLayout.setVisibility(View.GONE)
        }
    }

    private WindowManager.LayoutParams getParams(int width, int height) {
        return new WindowManager.LayoutParams(width, height, type, flags, PixelFormat.TRANSLUCENT)
    }

    public void add(ViewName... viewsNames) {
        for (ViewName viewName : viewsNames) {
            switch (viewName) {
                case BUTTONS:
                    windowManager.addView(buttonsView, buttonsParams)
                    changeStopButtonPosition(true)
                    break
                case STOP:
                    windowManager.addView(stopView, stopParams)
                    break
                case RETRY:
                    windowManager.addView(retryView, retryParams)
                    break
                case PROGRESS_BAR:
                    windowManager.addView(progressBarView, progressBarParams)
                    changeStopButtonPosition(false)
                    break
                case CARDS:
                    windowManager.addView(cardsView, cardsParams)
                    break
                case TEXTS:
                    windowManager.addView(textsView, textsParams)
                    break
                case ADD:
                    windowManager.addView(addView, addParams)
                    break
            }
        }
    }

    public void remove(ViewName... viewsNames) {
        for (ViewName viewName : viewsNames) {
            switch (viewName) {
                case BUTTONS:
                    remove(buttonsView)
                    break
                case STOP:
                    remove(stopView)
                    break
                case RETRY:
                    remove(retryView)
                    break
                case PROGRESS_BAR:
                    remove(progressBarView)
                    break
                case CARDS:
                    remove(cardsView)
                    break
                case TEXTS:
                    remove(textsView)
                    break
                case ADD:
                    remove(addView)
                    break
            }
        }
    }

    private void remove(View view) {
        if (view.isAttachedToWindow()) {
            windowManager.removeView(view)
        }
    }

    private void tryShowingButtons(boolean skipTokenCheck) {
        if (skipTokenCheck) {
            showButtons(true)
        } else {
            add(ViewName.PROGRESS_BAR)
            overlayService.checkTokens()
        }
    }

    public void showButtons(boolean enoughTokens) {
        remove(ViewName.PROGRESS_BAR)

        if (enoughTokens) {
            add(ViewName.BUTTONS)
        } else {
            Functions.showToast(overlayService, overlayService.getString(R.string.no_tokens))
            overlayService.stop(RetryValue.REOPEN_APP)
        }
    }

    private void changeStopButtonPosition(boolean left) {
        stopParams.y = left ? dimensions.display.buttonHeight + margin : margin

        remove(ViewName.STOP)
        add(ViewName.STOP)
    }

    private void showRetryButton(RetryValue retryValue) {
        if (retryParams.x == 0) {
            retryParams.x = stopView.getWidth() + stopView.getWidth() / 3
        }

        retryButton.setTag(retryValue)

        add(ViewName.RETRY)

        changeStopButtonPosition(false)
    }

    public void showCards(List<Card> cards, float confidence, boolean elixirCollectorFlag) {
        if (this.cards == null) {
            this.cards = data.getDimensions().cards
        }

        cardsParams.gravity = Gravity.TOP
        cardsParams.y = this.cards.y

        String stringConfidence = String.valueOf(confidence)
        stringConfidence = stringConfidence.replace(".0", "")
        confidenceText.setText(overlayService.getString(R.string.percentage, stringConfidence))

        cardAdapter = new CardAdapter(overlayService, cards, setting.elixirCounterMode, setting.allowCyclingCards)
        recyclerView.setAdapter(cardAdapter)
        recyclerView.setLayoutManager(new LinearLayoutManager(overlayService, RecyclerView.HORIZONTAL, false))

        resetCardsSubViews()

        remove(ViewName.BUTTONS, ViewName.PROGRESS_BAR)
        add(ViewName.CARDS)

        if (setting.elixirCounterMode || setting.showConfidence) {
            if (!setting.elixirCounterMode) {
                elixirText.setVisibility(View.GONE)
            }

            if (!setting.showConfidence) {
                confidenceText.setVisibility(View.GONE)
            }

            add(ViewName.TEXTS)
        }

        if (setting.alwaysShowManualElixirButton || (setting.elixirCounterMode && elixirCollectorFlag)) {
            add(ViewName.ADD)
        }
    }

    public void removeCard(int currentDeckSize) {
        if (viewsFlag && currentDeckSize == 4) {
            leftUpCardsSubViews()
        }
    }

    public void notifyChanges(int index) {
        if (index == 0) {
            cardAdapter.notifyDataSetChanged()
        } else if (index > 0) {
            cardAdapter.notifyItemChanged(index)
        } else {
            cardAdapter.notifyItemRemoved(Math.abs(index))
        }
    }

    private void resetCardsSubViews() {
        textsParams = getParams(WRAP_CONTENT, WRAP_CONTENT)
        addParams = getParams(WRAP_CONTENT, WRAP_CONTENT)

        textsParams.gravity = Gravity.TOP|(!setting.invertLayout ? Gravity.START : Gravity.END)
        addParams.gravity = Gravity.TOP|(!setting.invertLayout ? Gravity.END : Gravity.START)

        textsParams.x = addParams.x = Functions.dpToPixels(overlayService, 3)
        textsParams.y = addParams.y = cards.y + cards.height

        viewsFlag = true
    }

    private void leftUpCardsSubViews() {
        textsParams.x = cards.width - textsView.getWidth() / 2
        addParams.x = cards.width - addView.getWidth() / 2

        int difference
        difference = (textsView.getHeight() - cards.height) / 2
        textsParams.y = cards.y - difference
        difference = (addView.getHeight() - cards.height) / 2
        addParams.y = cards.y - difference

        if (textsView.isAttachedToWindow()) {
            windowManager.updateViewLayout(textsView, textsParams)
        }

        if (addView.isAttachedToWindow()) {
            windowManager.updateViewLayout(addView, addParams)
        }

        viewsFlag = false
    }

    public void setText(ViewName viewName, String text) {
        switch (viewName) {
            case MODE:
                modeButton.setText(text)
                break
            case ELIXIR:
                elixirText.setText(String.valueOf(text))
                break
        }
    }

    public void setInfoButtonLayout(String tag) {
        if (!tag.isEmpty()) {
            infoButtonLayout.setVisibility(View.VISIBLE)
            infoButtonLayout.setTag(tag)
        } else {
            infoButtonLayout.setVisibility(View.GONE)
        }
    }

    public boolean shouldStopService(RetryValue retryValue) {
        if (retryValue != RetryValue.NO_ACTION) {
            remove(ViewName.BUTTONS, ViewName.PROGRESS_BAR, ViewName.CARDS, ViewName.TEXTS, ViewName.ADD)
            showRetryButton(retryValue)
            return false
        }

        if (cardsView.isAttachedToWindow()) {
            remove(ViewName.CARDS, ViewName.TEXTS, ViewName.ADD)
            tryShowingButtons(false)
            return false
        } else if (progressBarView.isAttachedToWindow()) {
            overlayService.addTokenBack()

            remove(ViewName.PROGRESS_BAR)

            tryShowingButtons(true)
            return false
        }

        return true
    }
}