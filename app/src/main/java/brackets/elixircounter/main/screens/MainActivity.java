package brackets.elixircounter.main.screens

public class MainActivity {

    MainManager mainManager
    Data data
    Versions oldVersions, newVersions

    @SuppressWarnings("FieldCanBeLocal")
    private final int CALLS = 6
    float progress
    boolean dimensionsFlag
    double appVersion

    TextRoundCornerProgressBar progressBar

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()

        mainManager = new MainManager(this)
        data = Data.getDataInstance(this)

        progressBar = findViewById(R.id.progressBar)

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.VISIBLE)
            mainManager.start()
        }, 1500)
    }

    public void handleVersions(Versions oldVersions, Versions newVersions) {
        this.oldVersions = oldVersions
        this.newVersions = newVersions
        appVersion = Functions.getVersion(this)

        if (newVersions.app.mandatory > appVersion) {
            Functions.showDialog(this, MessageType.MANDATORY_UPDATE)
            return
        }

        if (newVersions.app.maintenance) {
            Functions.showDialog(this, MessageType.MAINTENANCE)
            return
        }

        mainManager.getModes()
        mainManager.getCards()
        mainManager.getDimensions(this)
    }

    public void increaseProgress() {
        progress += 1
        int visibleProgress = (int) (progress / CALLS * 100)

        progressBar.setProgress(visibleProgress)
        progressBar.setProgressText(getString(R.string.percentage, String.valueOf(visibleProgress)))

        if (progress == CALLS) {
            new Handler().postDelayed(() -> {
                overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.fixed)
                Intent intent = new Intent(this, !MainManager.newUser ? HomeActivity.class : TutorialActivity.class)
                intent.putExtra(FieldName.UPDATE.toString(), newVersions.app.optional > appVersion)
                intent.putExtra(FieldName.NEW_INSTANCE.toString(), true)
                startActivity(intent)

                finish()
            }, 500)
        }
    }

    private void setup() {
        if (!Functions.isInternetAvailable(this)) {
            Functions.showDialog(this, MessageType.NO_INTERNET)
            return
        }

        if (OverlayService.isRunning) {
            stopService(new Intent(this, OverlayService.class))
        }
    }
}