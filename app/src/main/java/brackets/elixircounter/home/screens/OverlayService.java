package brackets.elixircounter.home.screens

public class OverlayService {

    ViewsManager viewsManager
    ServiceManager serviceManager
    TokensManager tokensManager
    HomeActivity homeActivity
    Data data
    Setting setting
    List<Mode> modes

    int elixir, modeIndex
    public static boolean isRunning

    public void onCreate() {
        super.onCreate()

        viewsManager = new ViewsManager(this)
        serviceManager = new ServiceManager(this)
        tokensManager = new TokensManager()
        data = Data.getDataInstance(this)
        setting = Data.getSettingInstance(this)
        modes = data.getModes()

        isRunning = true
        viewsManager.setupViews()
        selectMode()
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, getPackageName())
                .setContentText(getString(R.string.elixir_counter_is_running))
                .setSmallIcon(R.drawable.icon_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)
            NotificationChannel notificationChannel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE)

            assert notificationManager != null
            notificationManager.createNotificationChannel(notificationChannel)
        }

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    public void onDestroy() {
        super.onDestroy()

        isRunning = false
        viewsManager.remove(ViewName.BUTTONS, ViewName.PROGRESS_BAR, ViewName.CARDS, ViewName.TEXTS, ViewName.ADD, ViewName.STOP, ViewName.RETRY)
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return new CustomBinder(this)
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openButton:
                String CLASH_ROYAL_PACKAGE = "com.supercell.clashroyale"
                Intent intent = getPackageManager().getLaunchIntentForPackage(CLASH_ROYAL_PACKAGE)

                if (intent != null) {
                    startActivity(intent)
                } else {
                    String link = "https://play.google.com/store/apps/details?id=" + CLASH_ROYAL_PACKAGE
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                }

                break
            case R.id.startButton:
                if (HomeActivity.mediaProjectionManager == null || HomeActivity.data == null || Data.getStatics() == null) {
                    stop(RetryValue.NO_ACTION)
                    return
                }

                HomeActivity.mediaProjection = HomeActivity.mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) HomeActivity.data.clone())

                viewsManager.remove(ViewName.BUTTONS)
                viewsManager.add(ViewName.PROGRESS_BAR)

                break
            case R.id.modeButton:
                selectMode()
                break
            case R.id.infoButtonLayout:
                Functions.showDialog(homeActivity, v.getTag().toString())
                break
            case R.id.stopButton:
                fullStop()
                break
            case R.id.retryButton:
                switch ((RetryValue) v.getTag()) {
                    case REOPEN_APP:
                        OverlayService.isRunning = false
                        fullStop()

                        intent = new Intent(this, HomeActivity.class)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        intent.putExtra(FieldName.WATCH_AD.toString(), true)
                        startActivity(intent)
                        break
                    case SHOW_BUTTONS:
                        viewsManager.remove(ViewName.RETRY)
                        viewsManager.showButtons(true)
                        break
                }
            case R.id.addButton:
                serviceManager.addElixir()
                break
        }
    }

    public void bindActivity(HomeActivity homeActivity) {

        this.homeActivity = homeActivity
    }
    public void unbindActivity() {
        homeActivity = null
    }

    private void selectMode() {
        Mode mode = modes.get(modeIndex)
        serviceManager.setMode(mode)
        modeIndex++

        if (modeIndex == modes.size()) {
            modeIndex = 0
        }

        viewsManager.setText(ViewName.MODE, mode.name)
        viewsManager.setInfoButtonLayout(mode.info)
    }

    public void showCards(List<Card> cards, float confidence, boolean elixirCollectorFlag) {
        viewsManager.showCards(cards, confidence, elixirCollectorFlag)
    }

    public void removeCard(int id) {
        int currentDeckSize = serviceManager.removeCard(id)
        viewsManager.removeCard(currentDeckSize)
    }

    public void notifyChanges(int index) {
        new Handler(getMainLooper()).post(() -> viewsManager.notifyChanges(index))
    }

    public void showElixir(int elixir) {
        this.elixir = elixir
        new Handler(getMainLooper()).post(() -> viewsManager.setText(ViewName.ELIXIR, String.valueOf(elixir)))
    }

    public int getElixir() {
        return elixir
    }

    private void fullStop() {
        stop(RetryValue.NO_ACTION)
    }

    public void stop(RetryValue retryValue) {
        new Handler(getMainLooper()).post(() -> {
            if (viewsManager.shouldStopService(retryValue)) {
                if (homeActivity != null) {
                    homeActivity.unbind()
                }

                stopSelf()
            }
        })
    }
}