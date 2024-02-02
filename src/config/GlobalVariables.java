package config;

public class GlobalVariables {

	@ConfigVariable(category = "setup")
	public static String UserID = "";
	
	@ConfigVariable(category = "setup")
	public static String BeatmapMirrorLink = "https://beatconnect.io/b/";
	
	@ConfigVariable(category = "speed")
	public static int BeatmapScanDelayMS = 1000;
	
	@ConfigVariable(category = "speed")
	public static int BeatmapDownloadThreads = 25;

}
