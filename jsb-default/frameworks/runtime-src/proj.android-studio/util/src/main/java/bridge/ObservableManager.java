package bridge;

import android.util.SparseArray;

import java.util.Observable;

public class ObservableManager {

	private static final String TAG = "ObservableManager";

	private static ObservableManager sInstance;

	private final SparseArray<Observable> mObservableSparseArray;

	private static synchronized ObservableManager init() {

		if (null == sInstance) {
			sInstance = new ObservableManager();
		}
		return sInstance;
	}

	public static ObservableManager getInstance() {

		if (null == sInstance) {
			init();
		}
		return sInstance;
	}

	private ObservableManager() {

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
