package com.android.Disaster_App.Entities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("serial")
public class Message implements Serializable{
	private static final String TAG = "Message";
	public static final int TEXT_MESSAGE = 1;
	
	private int mType;
	private String mText;
	private String chatName;
	private byte[] byteArray;
	private InetAddress senderAddress;
	private boolean isMine;

	//// MARK: 16/06/2018 stores a record of all users this message been to
	private ArrayList<String> user_record;

	//Getters and Setters
	public int getmType() { return mType; }
	public void setmType(int mType) { this.mType = mType; }
	public String getmText() { return mText; }
	public void setmText(String mText) { this.mText = mText; }
	public String getChatName() { return chatName; }
	public void setChatName(String chatName) { this.chatName = chatName; }
	public byte[] getByteArray() { return byteArray; }
	public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }
	public InetAddress getSenderAddress() { return senderAddress; }
	public void setSenderAddress(InetAddress senderAddress) { this.senderAddress = senderAddress; }
	public boolean isMine() { return isMine; }
	public void setMine(boolean isMine) { this.isMine = isMine; }
	public ArrayList<String> getUser_record() {
		return user_record;
	}
	public void setUser_record(String user_name) {
		this.user_record.add(user_name);
	}
	
	public Message(int type, String text, InetAddress sender, String name){
		mType = type;
		mText = text;	
		senderAddress = sender;
		chatName = name;
		user_record = new ArrayList<>();
	}
}
