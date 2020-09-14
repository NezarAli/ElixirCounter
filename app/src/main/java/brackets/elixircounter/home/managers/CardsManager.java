package brackets.elixircounter.home.managers

class CardsManager {

    Setting setting
    Deck deck
    List<Card> onCards, offCards

    boolean cycleWithoutElixir, elixirCollectorCheck
    private final int MIRROR_ID, ELIXIR_COLLECTOR

    public CardsManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager
        setting = Data.getSettingInstance(serviceManager.getContext())
        MIRROR_ID = serviceManager.getContext().getResources().getInteger(R.integer.mirror)
        ELIXIR_COLLECTOR = serviceManager.getContext().getResources().getInteger(R.integer.elixir_collector)
    }

    public void initializeDeck(Deck deck) {
        this.deck = deck
        onCards = deck.cards
        offCards = new ArrayList<>()
        elixirCollectorCheck = false

        serviceManager.showCards(onCards, deck.confidence, deck.elixirCollectorFlag)

        if (setting.elixirCounterMode) {
            checkElixir()
        } else if (setting.allowCyclingCards) {
            cycleWithoutElixir = true
        }
    }

    public int removeCard(int id) {
        int index = 0

        for (int i = 0 i < onCards.size() i++) {
            if (onCards.get(i).id == id) {
                if (cycleWithoutElixir) {
                    if (onCards.get(i).elixir == 0) {
                        return onCards.size()
                    }
                } else {
                    if (!onCards.get(i).available) {
                        return onCards.size()
                    }

                    serviceManager.setElixir(onCards.get(i).elixir)
                }

                index = i

                offCards.add(onCards.get(i))
                onCards.remove(i)
                serviceManager.notifyChanges(-i)

                break
            }
        }

        if (onCards.size() == 3) {
            onCards.add(index, offCards.get(0))
            serviceManager.notifyChanges(0)
            offCards.remove(0)
        }

        checkMirror()
        checkElixirCollector()
        checkElixir()

        return onCards.size()
    }

    private void checkMirror() {
        if (deck.mirrorFlag) {
            int index = getIndex(MIRROR_ID)
            if (index == -1) {
                return
            }

            Card mirror = onCards.get(index)
            Card mirrored = offCards.get(offCards.size() - 1)

            mirror.elixir = mirrored.elixir + 1
            mirror.mirroredImage = mirrored.image

            onCards.set(index, mirror)
            serviceManager.notifyChanges(index)
        }
    }

    private void checkElixirCollector() {
        if (deck.elixirCollectorFlag && !elixirCollectorCheck) {
            int index = getIndex(ELIXIR_COLLECTOR)
            if (index == -1) {
                return
            }

            List<Card> cards = Data.getDataInstance(serviceManager.getContext()).getCards()
            Card elixirCollector = new Card()

            for (Card card : cards) {
                if (card.id == ELIXIR_COLLECTOR) {
                    elixirCollector = onCards.get(index)
                    elixirCollector.elixir = card.elixir
                    break
                }
            }

            onCards.set(index, elixirCollector)
            serviceManager.notifyChanges(index)

            elixirCollectorCheck = true
        }
    }

    public void checkElixir() {
        if (onCards == null) {
            return
        }

        int elixir = serviceManager.getElixir()

        for (int i = 0 i < onCards.size() i++) {
            if (onCards.get(i).available != elixir >= onCards.get(i).elixir && onCards.get(i).elixir != 0) {
                onCards.get(i).available = elixir >= onCards.get(i).elixir
                serviceManager.notifyChanges(i)
            }
        }
    }

    private int getIndex(int id) {
        for (int i = 0 i < onCards.size() i++) {
            if (onCards.get(i).id == id) {
                return i
            }
        }

        return -1
    }
}