package com.lifedawn.capstoneapp.friends.myfriends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lifedawn.capstoneapp.R;
import com.lifedawn.capstoneapp.common.RecyclerViewItemDecoration;
import com.lifedawn.capstoneapp.common.interfaces.OnClickFriendItemListener;
import com.lifedawn.capstoneapp.common.interfaces.OnDbQueryCallback;
import com.lifedawn.capstoneapp.common.viewmodel.FriendViewModel;
import com.lifedawn.capstoneapp.databinding.FragmentFriendsBinding;
import com.lifedawn.capstoneapp.databinding.ItemViewFriendBinding;
import com.lifedawn.capstoneapp.room.dto.FriendDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
	private FragmentFriendsBinding binding;
	private FriendViewModel friendViewModel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentFriendsBinding.inflate(inflater);
		return binding.getRoot();
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
		
		
		binding.floatingActionBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
			}
		});
		
		binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.recyclerView.addItemDecoration(new RecyclerViewItemDecoration(getContext()));
		
		RecyclerViewAdapter adapter = new RecyclerViewAdapter();
		adapter.setOnClickFriendItemListener(new OnClickFriendItemListener() {
			@Override
			public void onClickedRemove(FriendDto friend, int position) {
				friendViewModel.delete(friend.getId(), new OnDbQueryCallback<Boolean>() {
					@Override
					public void onResult(Boolean e) {
					
					}
				});
				
				adapter.notifyItemRemoved(position);
			}
		});
		binding.recyclerView.setAdapter(adapter);
		
		friendViewModel.getAll(new OnDbQueryCallback<List<FriendDto>>() {
			@Override
			public void onResult(List<FriendDto> e) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.setFriends(e);
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		});
		
	}
	
	private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
		OnClickFriendItemListener onClickFriendItemListener;
		List<FriendDto> friends = new ArrayList<>();
		
		public void setFriends(List<FriendDto> friends) {
			this.friends = friends;
		}
		
		public void setOnClickFriendItemListener(OnClickFriendItemListener onClickFriendItemListener) {
			this.onClickFriendItemListener = onClickFriendItemListener;
		}
		
		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new RecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_friend, null));
		}
		
		@Override
		public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
			holder.onBind();
		}
		
		@Override
		public void onViewRecycled(@NonNull RecyclerViewAdapter.ViewHolder holder) {
			holder.clear();
			super.onViewRecycled(holder);
		}
		
		@Override
		public int getItemCount() {
			return friends.size();
		}
		
		private class ViewHolder extends RecyclerView.ViewHolder {
			private ItemViewFriendBinding binding;
			
			public ViewHolder(@NonNull View itemView) {
				super(itemView);
				binding = ItemViewFriendBinding.bind(itemView);
			}
			
			public void clear() {
			
			}
			
			public void onBind() {
				
				binding.removeBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onClickFriendItemListener.onClickedRemove(friends.get(getAdapterPosition()), getAdapterPosition());
					}
				});
				
			}
		}
	}
}