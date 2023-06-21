package com.example.instrukcije.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.instrukcije.Fragments.ChatsGroupFragment;
import com.example.instrukcije.Fragments.ChatsPrivateFragment;

public class MyPagerAdapter extends FragmentStateAdapter {

	String type;

	public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, String type) {
		super(fragmentActivity);
		this.type = type;
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		switch (position) {
			case 1:
				return new ChatsGroupFragment(type);

			default:
				return new ChatsPrivateFragment(type);
		}
	}


	@Override
	public int getItemCount() {
		return 2;
	}
}
