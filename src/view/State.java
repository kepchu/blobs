package view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import view.State.StateListener;
//This class stores state related to GUI
public class State {

	public interface StateListener {
		public void onStateChange(String key);
	}

	private static final Object LOCK = new Object();
	
	private static final List<StateListener> registeredListeners = new ArrayList<StateListener>();
	
	
	private static Map<String, Object> s = Collections.synchronizedMap(
			new HashMap<String, Object>());
	
	private static Map<String, Object> ds = new HashMap<String, Object>(); //default settings;
	static {
		ds.put("drawSideWalls", false);
		ds.put("darkMode", false);
		ds.put("drawingMode", false);
		ds.put("edgesLeft", null);
		ds.put("edgesRight", null);
		ds.put("edgesGround", null);
		ds.put("edgesBouncy", false);
		ds.put("gravityInCentre", false);
		ds.put("zoomToWindow", true);
//		ds.put("drawSideWalls", false);
//		ds.put("drawSideWalls", false);
//		ds.put(key, value);
//		ds.put(key, value);
//		ds.put(key, value);
//		ds.put(key, value);
//		ds.put(key, value);
//		ds.put(key, value);
//		ds.put(key, value);
//		ds.put(key, value);
//		ds.put(key, value);
		s.putAll(ds);
	}
	
	public static Map<String, Object> getState () {
		synchronized (LOCK) {
			return new HashMap<String, Object>(s);
		}		
	}
	
	public static synchronized void setState (Map<String, Object> in) {
		synchronized (LOCK) {
			s.clear();
			s.putAll(in);
		}		
	}
	
	public static synchronized void setDefaultState () {
		synchronized (LOCK) {
			s.clear();
			s.putAll(ds);
		}		
	}
	
	public static void put (String key, Object value) {
		System.out.println("state.put() - " + key);
		synchronized (LOCK) {
			s.put(key, value);
		}
		for (StateListener sl : registeredListeners) {
			sl.onStateChange(key);
		}
	}
	public static Object get (String key) {
		//System.out.println("state.get() - " + key);
		if (!s.containsKey(key)) {
			throw new IllegalArgumentException("No mappingt for key: " + key);
		}
		synchronized (LOCK) {
			return s.get(key);//returns null if no mapping
		}
	}
	
	public static void subscribe (StateListener stateListener) {
		registeredListeners.add(stateListener);		
	}
	public static boolean cancelSubscribtion(StateListener stateListener) {
		return registeredListeners.remove(stateListener);
	}
}
