package org.cocos2dx.bridge;

import android.util.SparseArray;

import java.util.Observable;

public class MessageManager {

	private static MessageManager sInstance;

	private final SparseArray<Observable> mObservableSparseArray;

	private static synchronized MessageManager init() {

		if (null == sInstance) {
			sInstance = new MessageManager();
		}
		return sInstance;
	}

	public static MessageManager getInstance() {

		if (null == sInstance) {
			init();
		}
		return sInstance;
	}

	private MessageManager() {

		mObservableSparseArray = new SparseArray<Observable>();
	}

	public synchronized <T extends Observable> T getObservable(Class<T> t) {

		Observable observable = mObservableSparseArray.get(t.getName()
				.hashCode());

		if (observable == null) {
			observable = createObservable(t);
		}

		if (observable == null) {
			return null;
		}

		try {
			return (T) observable;
		} catch (Exception ex) {
			clearAllObservable();
			observable = createObservable(t);
		}

		return (T) observable;
	}

	private <T extends Observable> T createObservable(Class<T> t) {
		try {
			T observable = t.newInstance();
			if (observable != null) {
				mObservableSparseArray.put(t.getName().hashCode(), observable);
			}
			return observable;
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	public synchronized void clearAllObservable() {

		if (null != mObservableSparseArray) {
			int size = mObservableSparseArray.size();
			for (int i = 0; i < size; i++) {
				Observable observable = mObservableSparseArray.valueAt(i);
				if (null != observable) {
					observable.deleteObservers();
				}
			}
			mObservableSparseArray.clear();
		}
	}
}
