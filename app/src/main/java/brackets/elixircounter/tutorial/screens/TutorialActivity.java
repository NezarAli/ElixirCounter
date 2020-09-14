package brackets.elixircounter.tutorial.screens

public class TutorialActivity {

    ViewPager2Adapter viewPager2Adapter
    OnPageChangeCallback onPageChangeCallback
    int index

    ViewPager2 viewPager2
    Button prev, next
    CircleIndicator3 circleIndicator3

    public final static int TYPE_TEXT = 1, TYPE_VIDEO = 2

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        viewPager2Adapter = new ViewPager2Adapter(this)
        TutorialFragment tutorialFragment
        Bundle bundle

        tutorialFragment = new TutorialFragment()
        bundle = new Bundle()
        bundle.putInt(FieldName.TYPE.toString(), TYPE_TEXT)
        tutorialFragment.setArguments(bundle)
        viewPager2Adapter.addFragment(tutorialFragment)

        tutorialFragment = new TutorialFragment()
        bundle = new Bundle()
        bundle.putInt(FieldName.TYPE.toString(), TYPE_VIDEO)
        tutorialFragment.setArguments(bundle)
        viewPager2Adapter.addFragment(tutorialFragment)

        onPageChangeCallback = new OnPageChangeCallback() {
            public void onPageSelected(int position) {
                super.onPageSelected(position)
                index = position

                if (index == viewPager2Adapter.getItemCount() - 1) {
                    next.setText(getString(R.string.done))
                } else {
                    next.setText(getString(R.string.next))
                }
            }
        }

        viewPager2 = findViewById(R.id.viewPager2)
        prev = findViewById(R.id.prev)
        next = findViewById(R.id.next)
        circleIndicator3 = findViewById(R.id.circleIndicator3)

        viewPager2.setAdapter(viewPager2Adapter)
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
        prev.setOnClickListener(this)
        next.setOnClickListener(this)
        circleIndicator3.setViewPager(viewPager2)
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev:
                if (index > 0) {
                    viewPager2.setCurrentItem(index - 1, true)
                }
                break
            case R.id.next:
                if (index < viewPager2Adapter.getItemCount() - 1) {
                    viewPager2.setCurrentItem(index + 1, true)
                } else {
                    if (getIntent().getBooleanExtra(FieldName.NEW_INSTANCE.toString(), false)) {
                        Intent intent = new Intent(this, HomeActivity.class)
                        intent.putExtra(FieldName.UPDATE.toString(), getIntent().getBooleanExtra(FieldName.UPDATE.toString(), false))
                        startActivity(intent)
                    }

                    finish()
                }

                break
        }
    }
}