package com.example.instrukcije.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instrukcije.Adapters.MyPagerAdapter;
import com.example.instrukcije.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ChatsFragment extends Fragment {

	String type;

	public ChatsFragment(String type) {
		this.type = type;
	}

	TabLayout tabLayout;
	ViewPager2 viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chats, container, false);

		tabLayout = view.findViewById(R.id.tabLayout);
		viewPager = view.findViewById(R.id.viewPager);

		MyPagerAdapter adapter = new MyPagerAdapter(getActivity(), type);
		viewPager.setAdapter(adapter);

		new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
			switch (position) {
				case 0:
					tab.setText("Privatno");
					break;

				case 1:
					tab.setText("Grupe");
					break;
			}
		}).attach();

		return view;
	}
}