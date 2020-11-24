package com.android.Disaster_App.CustomAdapters;

import android.content.Context;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.Disaster_App.ChatActivity;
import com.android.Disaster_App.Entities.Message;
import com.android.Disaster_App.R;

import java.util.List;

public class ChatAdapter extends BaseAdapter {
	public static String TAG = "ChatAdapter";
	private List<Message> listMessage;
	private LayoutInflater inflater;
	private Context mContext;
	Message mes;

	public ChatAdapter(Context context, List<Message> listMessage){
		this.listMessage = listMessage;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return listMessage.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		mes = listMessage.get(position);
		int type = mes.getmType();
		if(view == null){
			CacheView cache = new CacheView();            
            
			view = inflater.inflate(R.layout.chat_row, null);
			cache.chatName = (TextView) view.findViewById(R.id.chatName);
            cache.text = (TextView) view.findViewById(R.id.text);
            cache.relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
			cache.llt_chatinfo = view.findViewById(R.id.lltrow_path);
			cache.tv_path = view.findViewById(R.id.tvrow_path);
	            
			view.setTag(cache);
		}
        final CacheView cache = (CacheView) view.getTag();
        cache.chatName.setText(listMessage.get(position).getChatName());
        cache.chatName.setTag(cache);
		StringBuilder foo = new StringBuilder();
        if (mes.getUser_record()!=null){
        	for (String bar : mes.getUser_record()){
        		foo.append(bar).append("\n");
			}
		}
		cache.tv_path.setText(foo.toString());
        cache.chatName.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				CacheView cache = (CacheView) v.getTag();
				((ChatActivity)mContext).talkTo((String) cache.chatName.getText());
				return true;
			}
		});
        cache.chatName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cache.llt_chatinfo.getVisibility() == View.GONE){
					cache.llt_chatinfo.setVisibility(View.VISIBLE);
				}else{
					cache.llt_chatinfo.setVisibility(View.GONE);
				}
			}
		});
        
        //Colourise differently own message
        if((Boolean) listMessage.get(position).isMine()){
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble_mine));
        }   
        else{
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble));
        }
        
        //We disable all the views and enable certain views depending on the message's type
        disableAllMediaViews(cache);
        
        /***********************************************
          				Text Message
         ***********************************************/
        if(type == Message.TEXT_MESSAGE){           
        	enableTextView(cache, mes.getmText());
		}
		return view;
	}
	
	private void disableAllMediaViews(CacheView cache){
		cache.text.setVisibility(View.GONE);
	}
	
	private void enableTextView(CacheView cache, String text){
		if(!text.equals("")){
			cache.text.setVisibility(View.VISIBLE);
			cache.text.setText(text);
			Linkify.addLinks(cache.text, Linkify.PHONE_NUMBERS);
			Linkify.addLinks(cache.text, Patterns.WEB_URL, "myweburl:");
		}		
	}

	//Cache
	private static class CacheView{
		public TextView chatName;
		public TextView text;
		public ImageView image;
		public RelativeLayout relativeLayout;
		public ImageView audioPlayer;
		public ImageView videoPlayer;
		public ImageView videoPlayerButton;
		public ImageView fileSavedIcon;
		public TextView fileSaved;
		//
		public LinearLayout llt_chatinfo;
		public TextView tv_path;
	}
}
