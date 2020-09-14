package brackets.elixircounter.home.utilities

public class CustomBinder {

    OverlayService overlayService

    public CustomBinder(OverlayService overlayService) {
        this.overlayService = overlayService
    }

    public OverlayService getOverlayService() {
        return overlayService
    }
}