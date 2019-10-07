package io.github.talkarcabbage.rstimer.newtimers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import io.github.talkarcabbage.logger.LoggerManager;

public abstract class NewTimer {
	
	public static final long DAY_LENGTH_MILLIS = 86400000;
	public static final long WEEK_LENGTH_MILLIS = 604800000;
	static Logger logger = LoggerManager.getInstance().getLogger("Timer");
	
	public static final String MAP_NAME = "name";
	public static final String MAP_AUDIO = "audio";
	public static final String MAP_TAB = "tab";
	public static final String MAP_DURATION = "duration";
	public static final String MAP_LATEST_RESET = "latestreset";
	
	boolean audio = false;
	volatile String name;
	int tabID = 0;

	public NewTimer(String name, int tabID, boolean audio) {
		this.name = name;
		this.tabID = tabID;
		this.audio = audio;
	}
	
	public NewTimer(Map<String, String> dataMap) {
		this.name = "MISSING"; //Some arbitrary defaults in case of missing data
		this.tabID = 0;
		this.audio = false;
		
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			switch (entry.getKey()) { //NOSONAR we don't handle all cases here on purpose, thanks
			case MAP_NAME: 
				this.name = entry.getValue();
				break;
			case MAP_TAB: 
				this.tabID = Integer.parseInt(entry.getValue());
				break;
			case MAP_AUDIO:
				this.audio = Boolean.parseBoolean(entry.getValue());
				break;
			}
		}
	}
		
	/**
	 * Used by timers that require special actions when the GUI ticks.
	 * Example usage is to reset a self-repeating timer on completion.
	 */
	public void tickTimer() {}
	
	/**
	 * Called when the timer needs to be reset. 
	 */
	public abstract void resetTimer();
	
	/**
	 * Called when the timer needs to be set as complete.
	 */
	public abstract void resetTimerComplete();
	
	
	/**
	 * Returns the pseudojsonyaml representation of this timer, for saving.
	 * This method does not need to be overridden typically.
	 * @return The string representation of this Timer, for saving.
	 */
	public String getTimerSaveString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t" + getTimerTypeString() + " {" + "\n");
		Map<String, String> map = getTimerDataMap();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append("\t\t" + entry.getKey() + ":" + entry.getValue() + "\n");
		}
		sb.append("\t" + "}" + "\n");
		return sb.toString();
	}
	
	/**
	 * Returns a String representing the timer's type for generating save-file entries.
	 * @return The type String
	 */
	public abstract String getTimerTypeString();
	
	/**
	 * Returns a map containing the data associated with this timer.
	 * This is used for mapping the data to a save-file entry.
	 * This should be overridden and super()'d plus any additional data.
	 * @return The Map of data in the format variable,value
	 */
	public Map<String, String> getTimerDataMap() {
		HashMap<String, String> map = new HashMap<>(8);
		map.put(MAP_NAME, this.name);
		map.put(MAP_TAB, String.valueOf(this.getTab()));
		map.put(MAP_AUDIO, String.valueOf(this.audio));
		return map;
	}
	
	
	/**
	 * Returns the tooltip text for this timer, for display on the GUI.
	 * @return
	 */
	public abstract String getTooltipText();
	
	/**
	 * Used to fill in the progress bar
	 * @return Progress percentage
	 */
	public abstract int getPercentageComplete();
	
	/**
	 * Used to get the time remaining for the tooltip
	 * @return The remaining time.
	 */
	public abstract long getTimeRemaining();
		
	/*
	 * Begin common Timer methods (implemented)
	 */
	
	/**
	 * Returns the tab on the GUI this timer is shown on
	 * @return
	 */
	public int getTab() {
		return tabID;
	}
	
	/**
	 * Sets the tab on the GUI this timer is shown on
	 * @param tabID The tab to show this timer on.
	 */
	public void setTab(int tabID) {
		this.tabID = tabID;
	}
	
	/**
	 * Returns the name of this timer, for display on the GUI
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this timer, for display on the GUI
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Returns the Audio string for this timer. "none" indicates a lack of audio.
	 * @return
	 */
	public boolean getAudio() {
		return audio;
	}

	/**
	 * Sets the audio string for this timer, used to determine the name of the audio file to play for sound alerts.
	 * "none" indicates a lack of audio.
	 * @param audio
	 */
	public void setAudio(boolean audio) {
		this.audio = audio;
	}


	/**
	 * Format the given time value in ms for display in hh:mm:ss format on the GUI tooltips.
	 * @param time Time in ms
	 * @return The formatted time string
	 */
	public static String formatTimeRemaining(long time) {
		if (time <= 0) {
			return "Complete!";
		} else {
			long timeSeconds = time/1000;
			return Math.round(Math.floor((double)timeSeconds/3600)) + ":" + (long)Math.floor(((double)timeSeconds%3600)/60) + ":" + timeSeconds%60;
		}
	}
	
	public static long sanitizeValueRangeMin(long min, long value) {
		return value>min ? value : min;
	}
	
	public static long sanitizeValueRangeMax(long max, long value) {
		return value<max ? value : max;
	}
	
}
