package eniac.util;

public enum Status {

	/**
	 * the active configuration or null
	 */
	CONFIGURATION, 
	
	/**
	 * flag indicating, whether the overview window is shown or not
	 */
	SHOW_OVERVIEW, 
	
	/**
	 * the default height of the configuration panel
	 */
	BASIC_CONFIGURATION_HEIGHT,
	
	/**
	 * the height of the configuration panel 
	 */
	ZOOMED_HEIGHT, 
	
	/**
	 * flag indicating, whether the overview window is shown or not
	 */
	SHOW_LOG, 
	
	/**
	 * the currently loaded skin (currently only buttercream) or null
	 */
	SKIN, 
	
	/**
	 * the currently loaded language
	 */
	LANGUAGE, 
	
	/**
	 * flag indicating, whether the pulses shall be highlighted or not
	 */
	HIGHLIGHT_PULSE, 
	
	/**
	 * the current simulation time
	 */
	SIMULATION_TIME;

	
}
