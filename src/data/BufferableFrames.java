package data;

import java.util.List;

public interface BufferableFrames {
	
		public void advanceFrame();
		public boolean isFull();
		public boolean isEmpty();
		public int maxSize();
		public int currentSize();//big -> hint to speed-up gfx rendering
		
		public FrameData getFrame();
		
}
