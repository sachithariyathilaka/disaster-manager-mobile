package com.android.Disaster_App;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.Disaster_App.AsyncTasks.SendMessageClient;
import com.android.Disaster_App.AsyncTasks.SendMessageServer;
import com.android.Disaster_App.CustomAdapters.ChatAdapter;
import com.android.Disaster_App.Entities.Message;
import com.android.Disaster_App.Receivers.WifiDirectBroadcastReceiver;
import com.android.Disaster_App.util.ActivityUtilities;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {
	private static final String TAG = "ChatActivity";
	private static final int DELETE_MESSAGE = 101;
	private static final int COPY_TEXT = 103;
	private static final int SHARE_TEXT = 104;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private EditText edit;
	private static ListView listView;
	private static List<Message> listMessage;
	private static ChatAdapter chatAdapter;
	Double latitude, longitude;
	FusedLocationProviderClient fusedLocationClient;
	private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
	String HOST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmActivity(this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		//fetchLocation();
        //Start the service to receive message
        startService(new Intent(this, MessageService.class));
        //Initialize the adapter for the chat
        listView = (ListView) findViewById(R.id.messageList);
        listMessage = new ArrayList<Message>();
        chatAdapter = new ChatAdapter(this, listMessage);
        listView.setAdapter(chatAdapter);
		edit = (EditText) findViewById(R.id.editMessage);

		//Send a message
        Button button = (Button) findViewById(R.id.sendMessage);
        button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendData();
				if(!edit.getText().toString().equals("")){
					sendMessage(Message.TEXT_MESSAGE);
				}
				else{
					Toast.makeText(ChatActivity.this, "Please enter a not empty message", Toast.LENGTH_SHORT).show();
				}
			}
		});

        //Register the context menu to the list view (for pop up menu)
        registerForContextMenu(listView);
	}

	private void fetchLocation() {
		if (ContextCompat.checkSelfPermission(ChatActivity.this,
				Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this,
					Manifest.permission.ACCESS_COARSE_LOCATION)) {

				new AlertDialog.Builder(this).
						setTitle("Required Location Permission")
						.setMessage("You have to give this Permission to access the feature")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								ActivityCompat.requestPermissions(ChatActivity.this,
										new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
										MY_PERMISSIONS_REQUEST_READ_CONTACTS);

							}
						}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
						.create()
						.show();
			} else {

				ActivityCompat.requestPermissions(ChatActivity.this,
						new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
						MY_PERMISSIONS_REQUEST_READ_CONTACTS);
			}
		} else {


			fusedLocationClient.getLastLocation()
					.addOnSuccessListener(this, new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							if (location != null) {
								latitude=location.getLatitude();
								longitude=location.getLongitude();
							}
						}
					});
		}
	}

	private void sendData() {
		String message = edit.getText().toString().trim();
		String id = MainActivity.loadChatName(this);
		String Longitude = "79.12";
		String Latitude = "79.12";
		HOST = "http://18.216.245.34/androidApp/saveData.php?id="+id+"&message="+message+"&longitude="+Longitude+"&latitude="+Latitude;

		StringRequest stringRequest=new StringRequest(Request.Method.GET, HOST,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject=new JSONObject(response);
							String Output=jsonObject.getString("Output");

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(ChatActivity.this, "Error Occured due to "+ e.toString(), Toast.LENGTH_SHORT).show();
						}

					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Toast.makeText(ChatActivity.this, "Error Occured "+ error.toString(), Toast.LENGTH_SHORT).show();

			}
		});
		RequestQueue requestQueue= Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		ActivityUtilities.customiseActionBar(this);
	}

	@Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				Log.v(TAG, "Discovery process succeeded");
			}

			@Override
			public void onFailure(int reason) {
				Log.v(TAG, "Discovery process failed");
			}
		});
		saveStateForeground(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        saveStateForeground(false);
    }

	// Hydrate Message object then launch the AsyncTasks to send it
	public void sendMessage(int type){ ;
		// Message written in EditText is always sent
		Message mes = new Message(type, edit.getText().toString(), null, MainActivity.chatName);
		mes.setUser_record(MainActivity.loadChatName(this));
		if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
			Log.e(TAG, "Message hydrated, start SendMessageServer AsyncTask");

			new SendMessageServer(ChatActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
		else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
			Log.e(TAG, "Message hydrated, start SendMessageClient AsyncTask");

			new SendMessageClient(ChatActivity.this, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}

		edit.setText("");
	}

	// Refresh the message list
	public static void refreshList(Message message, boolean isMine){
//		Log.v(TAG, "Refresh message list starts");

		message.setMine(isMine);
//		Log.e(TAG, "refreshList: message is from :"+message.getSenderAddress().getHostAddress() );
//		Log.e(TAG, "refreshList: message is from :"+isMine );
		listMessage.add(message);
    	chatAdapter.notifyDataSetChanged();

//    	Log.v(TAG, "Chat Adapter notified of the changes");

    	//Scroll to the last element of the list
    	listView.setSelection(listMessage.size() - 1);
    }

	// Save the app's state (foreground or background) to a SharedPrefereces
	public void saveStateForeground(boolean isForeground){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
  		Editor edit = prefs.edit();
  		edit.putBoolean("isForeground", isForeground);
  		edit.commit();
	}

    //Create pop up menu for image download, delete message, etc...
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options");

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        Message mes = listMessage.get((int) info.position);

        //Option to delete message independently of its type
        menu.add(0, DELETE_MESSAGE, Menu.NONE, "Delete message");

        if(!mes.getmText().equals("")){
        	//Option to copy message's text to clipboard
            menu.add(0, COPY_TEXT, Menu.NONE, "Copy message text");
            //Option to share message's text
        	menu.add(0, SHARE_TEXT, Menu.NONE, "Share message text");
        }
    }

    //Handle click event on the pop up menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case DELETE_MESSAGE:
            	deleteMessage(info.id);
            	return true;

            case COPY_TEXT:
            	copyTextToClipboard(info.id);
            	return true;

            case SHARE_TEXT:
            	shareMedia(info.id, Message.TEXT_MESSAGE);
            	return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    //Delete a message from the message list (doesn't delete on other phones)
    public void deleteMessage(long id){
    	listMessage.remove((int) id);
    	chatAdapter.notifyDataSetChanged();
    }

    public void talkTo(String destination){
    	edit.setText("@" + destination + " : ");
    	edit.setSelection(edit.getText().length());
    }

    private void copyTextToClipboard(long id){
    	Message mes = listMessage.get((int) id);
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("message", mes.getmText());
		clipboard.setPrimaryClip(clip);
		Toast.makeText(this, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareMedia(long id, int type){
    	Message mes = listMessage.get((int) id);

    	switch(type){
    		case Message.TEXT_MESSAGE:
				Intent sendIntent = new Intent();
    	    	sendIntent.setAction(Intent.ACTION_SEND);
    	    	sendIntent.putExtra(Intent.EXTRA_TEXT, mes.getmText());
    	    	sendIntent.setType("text/plain");
    	    	startActivity(sendIntent);
    	}
    }


}
