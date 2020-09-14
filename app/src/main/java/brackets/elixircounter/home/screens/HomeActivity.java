package brackets.elixircounter.home.screens

public class HomeActivity  {

    public static Intent data
    TokensManager tokensManager
    OverlayService overlayService
    Intent intent
    RewardedAd rewardedAd

    private final int OVERLAY_REQUEST_CODE = 1, MEDIA_PROJECTION_REQUEST_CODE = 2
    boolean startServiceAfterTokenCheck, reachedTokenLimit

    Button tutorial, settings, addToken, start
    TextView textView
    ProgressBar progressBar
    AdView adView

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (getIntent().getBooleanExtra(FieldName.UPDATE.toString(), false)) {
            Functions.showDialog(this, MessageType.OPTIONAL_UPDATE)
        }

        tokensManager = new TokensManager()

        tutorial = findViewById(R.id.tutorial)
        settings = findViewById(R.id.settings)
        addToken = findViewById(R.id.addToken)
        start = findViewById(R.id.start)
        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        adView = findViewById(R.id.adView)

        tutorial.setOnClickListener(this)
        settings.setOnClickListener(this)
        addToken.setOnClickListener(this)
        start.setOnClickListener(this)

        loadAd()
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    protected void onResume() {
        super.onResume()

        if (getIntent().getBooleanExtra(FieldName.WATCH_AD.toString(), false)) {
            Functions.showDialog(this, MessageType.WATCH_AD)
            getIntent().putExtra(FieldName.WATCH_AD.toString(), false)
        }
    }

    public void onBackPressed() {
        Functions.showDialog(this, MessageType.EXIT)
    }

    protected void onDestroy() {
        if (OverlayService.isRunning) {
            unbind()
        }

        super.onDestroy()
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data)

        switch (requestCode) {
            case OVERLAY_REQUEST_CODE:
                if (gotPermission(OVERLAY_REQUEST_CODE)) {
                    if (gotPermission(MEDIA_PROJECTION_REQUEST_CODE)) {
                        start()
                    } else {
                        askForPermission(MEDIA_PROJECTION_REQUEST_CODE)
                    }
                }
                break
            case MEDIA_PROJECTION_REQUEST_CODE:
                HomeActivity.data = data

                if (gotPermission(MEDIA_PROJECTION_REQUEST_CODE)) {
                    if (gotPermission(OVERLAY_REQUEST_CODE)) {
                        start()
                    } else {
                        askForPermission(OVERLAY_REQUEST_CODE)
                    }
                }
                break
        }
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        overlayService = ((CustomBinder) iBinder).getOverlayService()
        overlayService.bindActivity(this)
    }

    public void onServiceDisconnected(ComponentName componentName) {
        unbind()
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tutorial:
                if (OverlayService.isRunning) {
                    stopAndUnbind()
                }

                startActivity(new Intent(this, TutorialActivity.class))
                break
            case R.id.settings:
                if (OverlayService.isRunning) {
                    stopAndUnbind()
                }

                Data.resetDataInstance()
                startActivity(new Intent(this, SettingsActivity.class))
                break
            case R.id.addToken:
                if (OverlayService.isRunning) {
                    stopAndUnbind()
                }

                showAd()
                break
            case R.id.start:
                startServiceAfterTokenCheck = true
                progressBar.setVisibility(View.VISIBLE)
                break
        }
    }

    private void loadAd() {
        adView.loadAd(new AdRequest.Builder().build())

        RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback() {
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                Functions.recordException(new Exception(loadAdError.getMessage()))
            }
        }

        rewardedAd = new RewardedAd(this, getString(R.string.video_ad_id))
        rewardedAd.loadAd(new AdRequest.Builder().build(), rewardedAdLoadCallback)
    }

    public void showAd() {
        if (reachedTokenLimit) {
            Functions.showDialog(this, MessageType.TOKENS_LIMIT)
            return
        }

        if (rewardedAd.isLoaded()) {
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    progressBar.setVisibility(View.VISIBLE)
                }

                public void onRewardedAdFailedToShow(AdError adError) {

                }

                public void onRewardedAdOpened() {
                    loadAd()
                }
            }

            rewardedAd.show(this, rewardedAdCallback)
        }
    }

    public void start() {
        if (OverlayService.isRunning) {
            stopAndUnbind()
            return
        }

        if (Data.getDataInstance(this).getAskForPermissions()) {
            Functions.showDialog(this, MessageType.ASK_PERMISSION)
            Data.getDataInstance(this).setAskForPermissions()
            return
        } else if (!gotPermission(OVERLAY_REQUEST_CODE) || !gotPermission(MEDIA_PROJECTION_REQUEST_CODE)) {
            startAskingForPermissions()
            return
        }

        intent = new Intent(this, OverlayService.class)
        ContextCompat.startForegroundService(this, intent)
        bindService(intent, this, Context.BIND_AUTO_CREATE)

        changeAdStatus(false)
        start.setText(getString(R.string.stop))
    }

    public void startAskingForPermissions() {
        if (!gotPermission(OVERLAY_REQUEST_CODE)) {
            askForPermission(OVERLAY_REQUEST_CODE)
        } else {
            askForPermission(MEDIA_PROJECTION_REQUEST_CODE)
        }
    }

    private boolean gotPermission(int code) {
        switch (code) {
            case OVERLAY_REQUEST_CODE:
                return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
            case MEDIA_PROJECTION_REQUEST_CODE:
                return data != null
        }

        return false
    }

    @SuppressLint("InlinedApi")
    private void askForPermission(int code) {
        switch (code) {
            case OVERLAY_REQUEST_CODE:
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))
                startActivityForResult(intent, OVERLAY_REQUEST_CODE)
                break
            case MEDIA_PROJECTION_REQUEST_CODE:
                mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)
                assert mediaProjectionManager != null
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), MEDIA_PROJECTION_REQUEST_CODE)
                break
        }
    }

    public void stopAndUnbind() {
        unbind()
        stopService(intent)
    }

    public void unbind() {
        unbindService(this)
        overlayService.unbindActivity()

        changeAdStatus(true)
        start.setText(getString(R.string.start))
    }

    private void changeAdStatus(boolean flag) {
        if (flag) {
            adView.resume()
            adView.setVisibility(View.VISIBLE)
        } else {
            adView.pause()
            adView.setVisibility(View.GONE)
        }
    }
}