package app;

import java.io.Serializable;
import java.util.Map;

import data.FrameData;

class SaveFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Map<String, Object> state;
	private final FrameData data;
	public SaveFile(FrameData data, Map<String, Object> state) {
		super();
		this.state = state;
		this.data = data;
	}
	
	public Map<String, Object> getState() {
		return state;
	}
	public FrameData getData() {
		return data;
	}
	
}