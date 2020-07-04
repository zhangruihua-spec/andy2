package com.util.observable;

import java.util.Observable;

public class UploadAvatarObservable extends Observable {


	private int avatarType;

	public void notifyUploadAvatar() {
		setChanged();
		notifyObservers(avatarType);
	}
	public void setAvatarType(int type) {
		avatarType = type;
	}
}
