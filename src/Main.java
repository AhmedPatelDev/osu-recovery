import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import beat.BeatDownloadManager;
import beat.BeatMap;
import beat.BeatUtils;
import config.ConfigManager;
import config.GlobalVariables;

public class Main {

	static String DOWNLOAD_FOLDER = "downloads//";
	
	public static ArrayList<Integer> getLocalMaps() {
		ArrayList<Integer> songs = new ArrayList<Integer>();
		String localSongsDir = "\\osu!\\Songs";
		
		File folder = new File(localSongsDir);
		File[] listOfFiles = folder.listFiles();
		
		for(File file : listOfFiles) {
			if(file.isDirectory()) {
				String fileName = "";
				if(file.getName().contains(" ")) {
					fileName = file.getName().split(" ")[0];
				} else {
					fileName = file.getName();
				}
				
				try {
					int beatmap_id = Integer.parseInt(fileName);
					
					if(!songs.contains(beatmap_id)) {
						songs.add(beatmap_id);
					}
				} catch (NumberFormatException e) {}
			}
		}
		
		return songs;
	}
	
	public static void main(String[] args) throws Exception {
		ConfigManager configManager = new ConfigManager(GlobalVariables.class, "config");

		String UserID = "";
		
		Scanner scanner = new Scanner(System.in);

		System.out.println("-------------------------");
		System.out.println("|     osu!recovery      |");
		System.out.println("-------------------------");
		System.out.println();
		
		if (configManager.configFileExists()) {
			try {
				configManager.loadFile();
			} catch (Exception e) {
				System.out.println("Please enter the new UserID:");
				GlobalVariables.UserID = scanner.nextLine();
				configManager.saveFile();
			}
		} else {
			System.out.println("Please enter the new UserID:");
			GlobalVariables.UserID = scanner.nextLine();
			configManager.saveFile();
		}
		
		UserID = GlobalVariables.UserID;
		
		System.out.println("UserID is set to: " + UserID);
		
		String[] choices = {"Change", "Continue"};
		int input = getUserChoice(scanner, choices);
		
		if(input == 1) {
			System.out.println("Please enter the new UserID:");
			UserID = scanner.nextLine();
			configManager.saveFile();
		}
		
		System.out.println("");
		System.out.println("-- Settings --");
		System.out.println("UserID: " + GlobalVariables.UserID);
		System.out.println("BeatmapMirrorLink: " + GlobalVariables.BeatmapMirrorLink);
		System.out.println("BeatmapScanDelayMS: " + GlobalVariables.BeatmapScanDelayMS);
		System.out.println("BeatmapDownloadThreads: " + GlobalVariables.BeatmapDownloadThreads);
		System.out.println("");
		
		System.out.println("Finding beatmaps from UserID: " + UserID + "...");

		ArrayList<BeatMap> beats = BeatUtils.getUserBeatMaps(UserID + "");
		ArrayList<BeatMap> beatsWithoutDupes = BeatUtils.getBeatsWithoutDuplicates(beats);
		
		System.out.println("Total captured songs: " + beats.size());
		System.out.println("Total captured beatmapsets: " + beatsWithoutDupes.size());
		System.out.println("");
		
		ArrayList<BeatMap> beatsCleaned = BeatUtils.removeFromList(beatsWithoutDupes, BeatUtils.getIDsFromLocation(DOWNLOAD_FOLDER));
		
		System.out.println("Removed " + (beatsWithoutDupes.size() - beatsCleaned.size()) + " beatmap sets from list.");
		System.out.println();
				
		File dlFile = new File(DOWNLOAD_FOLDER);
		dlFile.mkdir();

		
		ArrayList<Integer> localSongs = getLocalMaps();
		ArrayList<BeatMap> beatsLocalRemoved = new ArrayList<BeatMap>();
		
		for(BeatMap beat : beatsCleaned) {
			boolean shouldAdd = true;
			for(int localBeatID : localSongs) {
				if(beat.getBeatmap_id() == localBeatID) {
					shouldAdd = false;
				}
			}
			
			if(shouldAdd) {
				beatsLocalRemoved.add(beat);
			}
		}
		
		System.out.println("Started downloading maps to: " + DOWNLOAD_FOLDER);

		downloadBeats(beatsLocalRemoved, () -> {
	        System.out.println("Finished downloading beatmap sets.");
	    });
	}
	
	public static int getUserChoice(Scanner scanner, String[] validInputs) {
	    boolean validInput = false;
	    int input = 0;

	    while (!validInput) {
	        for (int i = 0; i < validInputs.length; i++) {
	            System.out.println((i + 1) + ") " + validInputs[i]);
	        }

	        try {
	            input = scanner.nextInt();
	            scanner.nextLine();
	            if (input > 0 && input <= validInputs.length) {
	                validInput = true;
	            } else {
	                System.out.println("Invalid input. Please enter a valid input.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a valid input.");
	            scanner.nextLine();
	        }
	    }

	    return input;
	}
	
	public static void downloadBeats(ArrayList<BeatMap> beats, Runnable callback) {
	    BeatDownloadManager downloadManager = new BeatDownloadManager();
	    int totalTasks = beats.size();
	    AtomicInteger completedTasks = new AtomicInteger(0);

	    int NUM_PROGRESS_SECTIONS = 40;
	    
	    for (BeatMap beat : beats) {
	        String fileName = ((beat.getBeatmapset_id() + " " + beat.getArtist() + " - " + beat.getTitle())
	                .replaceAll("[\\\\/:*?\"<>|]", "") + ".osz").strip();

	        downloadManager.downloadMap(beat.getBeatmapset_id(), DOWNLOAD_FOLDER + fileName, () -> {
	        	int completed = completedTasks.incrementAndGet();
	            double percentComplete = (double) completed / totalTasks;
	            int numFilledSections = (int) (percentComplete * NUM_PROGRESS_SECTIONS);
	            int numEmptySections = NUM_PROGRESS_SECTIONS - numFilledSections;
	            String progressBar = "[" + "+".repeat(numFilledSections) + "-".repeat(numEmptySections) + "]";
	            
	            System.out.println("(" + completed + "/" + totalTasks + ") " + fileName);

	            if (completed == totalTasks) {
	                downloadManager.shutdown();
	                System.out.println("\nFinished downloading beatmap sets.");
	                callback.run();
	            }
	        });
	    }
	}
	
}
