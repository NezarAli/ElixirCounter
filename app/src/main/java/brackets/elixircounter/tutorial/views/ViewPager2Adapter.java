package brackets.elixircounter.tutorial.views

public class ViewPager2Adapter {

    private List<Fragment> fragments

    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity)
        fragments = new ArrayList<>()
    }

    @NonNull
    public Fragment createFragment(int position) {
        return fragments.get(position)
    }

    public int getItemCount() {
        return fragments.size()
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment)
    }
}