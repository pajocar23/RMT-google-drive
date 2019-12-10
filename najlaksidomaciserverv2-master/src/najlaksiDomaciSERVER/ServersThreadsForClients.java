package najlaksiDomaciSERVER;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class ServersThreadsForClients extends Thread {

	BufferedReader inputFromClient;
	PrintStream inputForClient;
	Socket socketForCommunication;

	String pathToFolder = "C:\\Users\\IRC_client\\Desktop\\RMT\\najlaksidomaciserverv2-master\\";

	String username;
	String password;
	boolean prem = false;
	int brojUploada = 0;

	public ServersThreadsForClients(Socket socketForCommunication) {
		this.socketForCommunication = socketForCommunication;
	}

	private void menu(BufferedReader inputFromClient, PrintStream inputForClient) throws Exception {
		String choice = null;
		boolean alreadyRegistered = false;

		inputForClient.println("****** MENU ******" + "\n\n1. REGISTER" + "\n2. LOGIN" + "\n3. Enter Shared Link"
				+ "\nTo Exit Application Type \"--exit\" At Any Time." + "\n\nYour choice: ");

		try {
			choice = inputFromClient.readLine();
		} catch (IOException e) {
			System.out.println("VEZA SA KLIJENTOM JE PUKLA...");
		}

		switch (choice) {
		case "1":
			if (alreadyRegistered) {
				login(inputFromClient, inputForClient);
			} else {
				register(inputFromClient, inputForClient);
			}
			break;
		case "2":
			login(inputFromClient, inputForClient);
			break;
		case "3":
			displayDisk();
			menu(inputFromClient, inputForClient);
			break;
		case "--exit":
			closeCommunication(inputFromClient, inputForClient);
			break;
		default:
			inputForClient.println("INVALID CHOICE.\n");
			menu(inputFromClient, inputForClient);
		}
	}
	
	int brojac =0;

	private void countFiles(BufferedReader inputFromClient, PrintStream inputForClient, File[] arr, int index,
			int level) {
		// terminate condition
				if (index == arr.length)
					return;


				// for files
				if (arr[index].isFile())
					brojac++;

				// for sub-directories
				else if (arr[index].isDirectory()) {
					countFiles(inputFromClient, inputForClient, arr[index].listFiles(), 0, level + 1);
				}

				// recursion for main directory
				countFiles(inputFromClient, inputForClient, arr, ++index, level);
	}
	
	private void uploadFiles() {
		/* MOZES PROMENITI DA TI IZLISTA DESKTOP AKO ZELIS */
//		String user = null;
//		try {
//
//			boolean isValid = false;
//			boolean usernameExists = false;
//			do {
//				inputForClient.println("Unesite link: ");
//				String link = inputFromClient.readLine();
//				if (!link.contains("DISK_LINK.mmklab") || !link.contains("https://")) {
//					inputForClient.println(
//							"Uneti link nije u dobrom formatu. Format linka: (https://usernameDISK_LINK.mmklab)");
//				} else {
//					user = link.substring(8, link.indexOf("DISK_LINK.mmklab"));
//					BufferedReader br = new BufferedReader(
//							new FileReader(pathToFolder + "RegistrovaniKorisnici\\podaci.txt"));
//					String line = br.readLine();
//					while ((line != null)) {
//						String[] info = line.split(",");
//						String username1 = info[0];
//
//						if (username1.equals(user)) {
//							usernameExists = true;
//							break;
//						}
//
//						line = br.readLine();
//					}
//
//					br.close();
//
//					if (usernameExists) {
//						isValid = true;
//						izvrsiListFiles(user);
//					} else {
//						inputForClient.println("\nUsername doesn't exist.");
//					}
//				}
//
//			} while (!isValid);

		///////////////////////////////

		try {	
			izvrsiBrojanje(this.username);
			
			System.out.println("\n\n\n" + brojac + prem);
			
			if (prem == true || (!prem && brojac < 5)) {
				inputForClient.println("Unesite putanju do foldera: ");
				String putanjaDoFoldera = inputFromClient.readLine();

				inputForClient.println("Unesite ime fajla: ");
				String imeFajla = inputFromClient.readLine();

				byte[] bFile = readBytesFromFile(
						"C:\\Users\\IRC_client\\Desktop\\RMT\\najlaksidomaciclientv2-master\\DESKTOP\\"
								+ putanjaDoFoldera + "\\" + imeFajla);

				Path path = Paths.get(pathToFolder + "RegistrovaniKorisnici\\" + this.username + "\\" + imeFajla);

				inputForClient.println(pathToFolder + this.username + "\\" + imeFajla);
				inputForClient.println("C:\\Users\\IRC_client\\Desktop\\RMT\\najlaksidomaciclientv2-master\\DESKTOP\\"
						+ putanjaDoFoldera + "\\" + imeFajla);

				Files.write(path, bFile);

				inputForClient.print("Done");

				for (int i = 0; i < bFile.length; i++) {
					inputForClient.print((char) bFile[i]);
				}

			} else{
				inputForClient.println("Sirotiljski korisnici mogu maksimalno 5 fajlova da aploaduju.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		///////////////////////////////

//		} catch (IOException e) {
//			inputForClient.println("Greska u displayDisk...");
//		}

	}

	private void displayDisk() {
		/* https://pajaDISK_LINK.mmklab */
		String user = null;
		try {

			boolean isValid = false;
			boolean usernameExists = false;
			do {
				inputForClient.println("Unesite link: ");
				String link = inputFromClient.readLine();
				if (!link.contains("DISK_LINK.mmklab") || !link.contains("https://")) {
					inputForClient.println(
							"Uneti link nije u dobrom formatu. Format linka: (https://usernameDISK_LINK.mmklab)");
				} else {
					user = link.substring(8, link.indexOf("DISK_LINK.mmklab"));
					BufferedReader br = new BufferedReader(
							new FileReader(pathToFolder + "RegistrovaniKorisnici\\podaci.txt"));
					String line = br.readLine();
					while ((line != null)) {
						String[] info = line.split(",");
						String username1 = info[0];

						if (username1.equals(user)) {
							usernameExists = true;
							break;
						}

						line = br.readLine();
					}

					br.close();

					if (usernameExists) {
						isValid = true;
						izvrsiListFiles(user);
					} else {
						inputForClient.println("\nUsername doesn't exist.");
					}
				}

			} while (!isValid);

			///////////////////////////////

			try {
				inputForClient
						.println("Da li zelite da downloadujete neki od foldera[da/ne] (UNETI PUTANJU DO NJEGA)\n\n");
				String odgovor = inputFromClient.readLine();
				inputForClient.println("Unesite putanju do foldera: ");
				String pathToFolderForDownload = inputFromClient.readLine();
				inputForClient.println("Unesite ime fajla: ");
				String imeFilea = inputFromClient.readLine();
				if (odgovor.equalsIgnoreCase("da")) {
					byte[] bFile = readBytesFromFile(pathToFolder + "RegistrovaniKorisnici\\" + user + "\\"
							+ pathToFolderForDownload + "\\" + imeFilea); // Umesto
					// Sve\\word1.docx
					// ide nesto
					// drugo
					Path path = Paths.get(
							"C:\\Users\\IRC_client\\Desktop\\RMT\\najlaksidomaciclientv2-master\\DESKTOP\\DOWNLOADED\\"
									+ imeFilea); // Umesto
					// Sve\\word3.docx

					Files.write(path, bFile);
					inputForClient.print("Done");

					for (int i = 0; i < bFile.length; i++) {
						inputForClient.print((char) bFile[i]);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			///////////////////////////////

		} catch (IOException e) {
			inputForClient.println("Greska u displayDisk...");
		}

	}

	// fja koja vraca niz bajtova za neki fajl
	private static byte[] readBytesFromFile(String filePath) {

		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;

		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return bytesArray;
	}

	private String generateLink() {
		return "https://" + this.username + "DISK_LINK" + ".mmklab";
	}

	private void loggedInMenu(BufferedReader inputFromClient, PrintStream inputForClient) throws Exception {
		String choice = null;

		if (prem == true) {
			inputForClient.println("LOGGED IN AS: \"" + this.username + " [preimum korisnik]" + "\"\n");
			inputForClient.println("****** MENU ******" + "\n\n1. LIST MY FILES" + "\n2. UPLOAD FILE"
					+ "\n3. SHARED DISKS" + "\n4. EDIT DIRECTORIUMS" + "\n5. IZABERITE ZELJENU DATOTEKU"
					+ "\n6. SHARE WITH USER" + "\n7. GENERATE LINK" + "\n8. LOGOUT"
					+ "\nTo Exit Application Type \"--exit\" At Any Time." + "\n\nYour choice: ");
		} else {
			inputForClient.println("LOGGED IN AS: \"" + this.username + " [sirotilja]" + "\"\n");
			inputForClient.println("****** MENU ******" + "\n\n1. LIST MY FILES" + "\n2. UPLOAD FILE"
					+ "\n3. SHARED DISKS" + "\n4. IZABERITE ZELJENU DATOTEKU" + "\n5. SHARE WITH USER"
					+ "\n6. GENERATE LINK" + "\n7. LOGOUT" + "\nTo Exit Application Type \"--exit\" At Any Time."
					+ "\n\nYour choice: ");
		}
		try {
			choice = inputFromClient.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (prem == true) {
			switch (choice) {
			case "1":
				izvrsiListFiles(this.username);
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "2":
				uploadFiles();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "3":
				sharedDisks();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "4":
				directoriumMenu(inputFromClient, inputForClient);
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "5":
				otvoriZeljeniFajl();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "6":
				shareWithUser();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "7":
				generateLink();
				inputForClient.println(generateLink());
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "8":
				logout(inputFromClient, inputForClient);
				break;
			case "--exit":
				closeCommunication(inputFromClient, inputForClient);
				break;
			default:
				inputForClient.println("INVALID CHOICE!\n");
				loggedInMenu(inputFromClient, inputForClient);
			}
		} else {
			switch (choice) {
			case "1":
				izvrsiListFiles(this.username);
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "2":
				uploadFiles();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "3":
				sharedDisks();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "4":
				otvoriZeljeniFajl();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "5":
				shareWithUser();
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "6":
				generateLink();
				inputForClient.println(generateLink());
				loggedInMenu(inputFromClient, inputForClient);
				break;
			case "7":
				logout(inputFromClient, inputForClient);
				break;
			case "--exit":
				closeCommunication(inputFromClient, inputForClient);
				break;
			default:
				inputForClient.println("INVALID CHOICE!\n");
				loggedInMenu(inputFromClient, inputForClient);
			}
		}
	}

	private void sharedDisks() throws IOException {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(pathToFolder + "RegistrovaniKorisnici\\sharedLinks.txt"));
			String line = br.readLine();
			boolean usernameExists = false;
			while ((line != null)) {
				String[] info = line.split(",");
				String komeJeSerovano = info[0];
				String user = info[1].substring(8, info[1].indexOf("DISK_LINK.mmklab"));

				if (komeJeSerovano.equals(this.username)) {
					usernameExists = true;
					inputForClient.println("DISK od " + user + ", sa deljenim linkom " + info[1] + "\n\n");
					izvrsiListFiles(user);
				}

				line = br.readLine();
			}

			if (!usernameExists)
				inputForClient.println("Niko vam nije serovao disk.");

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void shareWithUser() {
		boolean usernameExists = false;
		boolean isValid = false;
		try {
			do {
				inputForClient.println("Unesite ime korisnika s kojim zelite da serujete: ");
				String user = inputFromClient.readLine();
				BufferedReader br = new BufferedReader(
						new FileReader(pathToFolder + "RegistrovaniKorisnici\\podaci.txt"));
				String line = br.readLine();

				while ((line != null)) {
					String[] info = line.split(",");
					String username1 = info[0];

					if (username1.equals(user)) {
						usernameExists = true;
						break;
					}

					line = br.readLine();
				}

				br.close();

				if (usernameExists) {
					isValid = true;
					// pravi se novi fajl sa podatcima koji je u formatu IME->KomeJeSerovan i tako
					// upisuje za svaki share
					String filename = pathToFolder + "RegistrovaniKorisnici\\sharedLinks.txt";
					FileWriter fw = new FileWriter(filename, true);
					String link = "https://" + this.username + "DISK_LINK.mmklab";
					inputForClient.print(link);
					fw.write(user + "," + link + "\n"); // umesto this.username treba ici link koga trebam procitati iz
														// pdoaci.txt
					fw.close();
				} else {
					inputForClient.println("\nUsername doesn't exist.");
				}
			} while (!isValid);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// https://www.journaldev.com/864/java-open-file --> otvaranje fajla
	private void otvoriZeljeniFajl() {
		String odgovor;
		String putanjaDoFajla;

		try {

			boolean isValid = false;
			do {
				inputForClient.println("Unesite username ciji folder/fajl zelite da otvorite: ");
				odgovor = inputFromClient.readLine();
				BufferedReader br = new BufferedReader(
						new FileReader(pathToFolder + "RegistrovaniKorisnici\\podaci.txt"));
				String line = br.readLine();

				boolean usernameExists = false;
				while ((line != null)) {
					String[] info = line.split(",");
					String username1 = info[0];

					if (username1.equals(odgovor)) {
						usernameExists = true;
						isValid = true;
						break;
					}

					line = br.readLine();
				}

				br.close();

				if (!usernameExists)
					inputForClient.println("Korisnik kojeg ste uneli ne postoji...Pokusajte ponovo");
				else {
					boolean nepostojeciKorisnik = false;
					BufferedReader citajSharedLinks = new BufferedReader(
							new FileReader(pathToFolder + "RegistrovaniKorisnici\\sharedLinks.txt"));
					String linija = citajSharedLinks.readLine();
					while ((linija != null)) {
						String[] info = linija.split(",");
						String user = info[0];
						String ceoLink = info[1];
						String username1 = ceoLink.substring(8, ceoLink.indexOf("DISK_LINK.mmklab"));

						if (odgovor.equals(username1) && user.equals(this.username)) {
							nepostojeciKorisnik = true;
							izvrsiListFiles(odgovor);
							inputForClient.println(
									"\nUnesite putanju fajla u formatu, ***voditi racuna da ne treba unositi username folder, vec sve ispod njega (Folder\\"
											+ "\\fajl.extenzija): ");
							putanjaDoFajla = inputFromClient.readLine();
							File otvorenFajl = new File(
									pathToFolder + "RegistrovaniKorisnici\\" + odgovor + "\\" + putanjaDoFajla);
							Desktop desktop = Desktop.getDesktop();
							if (otvorenFajl.exists()) {
								desktop.open(otvorenFajl);
								break;
							} else {
								inputForClient.println("PUTANJA ne postoji!");
								break;
							}
						}
						linija = citajSharedLinks.readLine();

					}
					if (nepostojeciKorisnik == false)
						inputForClient.println("Korisnik " + odgovor + " Vam nije serovao nista!");
					citajSharedLinks.close();
				}
			} while (!isValid);
		} catch (IOException e) {
			inputForClient.println("Greska");
		}
	}

	private void izvrsiListFiles(String user) {
		// Provide full path for directory(change accordingly)
		String maindirpath = pathToFolder + "RegistrovaniKorisnici\\" + user;

		// File object
		File maindir = new File(maindirpath);

		if (maindir.exists() && maindir.isDirectory()) {
			// array for files and sub-directories
			// of directory pointed by maindir
			File arr[] = maindir.listFiles();

			inputForClient.println("**********************************************");
			inputForClient.println("Files from \"" + user + "\" directory: ");
			inputForClient.println("**********************************************");

			// Calling recursive method
			listFiles(inputFromClient, inputForClient, arr, 0, 0);
		}
	}
	
	private void izvrsiBrojanje(String user) {
		// Provide full path for directory(change accordingly)
		String maindirpath = pathToFolder + "RegistrovaniKorisnici\\" + user;

		// File object
		File maindir = new File(maindirpath);

		if (maindir.exists() && maindir.isDirectory()) {
			// array for files and sub-directories
			// of directory pointed by maindir
			File arr[] = maindir.listFiles();


			// Calling recursive method
			countFiles(inputFromClient, inputForClient, arr, 0, 0);
		}
	}

	private void directoriumMenu(BufferedReader inputFromClient, PrintStream inputForClient) throws Exception {
		String choice = null;

		inputForClient.println("LOGGED IN AS: \"" + this.username + "\"\n");

		inputForClient.println("****** MENU ******" + "\n\n1. CREATE DIRS" + "\n2. RENAME DIRS"
				+ "\n3. MOVE file to another DIRS" + "\n4. DELETE DIRS" + "\n5. LOGOUT"
				+ "\nTo Exit Application Type \"--exit\" At Any Time." + "\n\nYour choice: ");

		try {
			choice = inputFromClient.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		switch (choice) {
		case "1":
			createDirs();
			loggedInMenu(inputFromClient, inputForClient);
			break;
		case "2":
			renameDirs();
			loggedInMenu(inputFromClient, inputForClient);
			break;
		case "3":
			moveFile();
			loggedInMenu(inputFromClient, inputForClient);
			break;
		case "4":
			deleteDirs();
			loggedInMenu(inputFromClient, inputForClient);
			break;
		case "5":
			logout(inputFromClient, inputForClient);
			break;
		case "--exit":
			closeCommunication(inputFromClient, inputForClient);
			break;
		default:
			inputForClient.println("INVALID CHOICE!\n");
			loggedInMenu(inputFromClient, inputForClient);
		}
	}

	private void deleteDirs() {
		inputForClient.println("\nUnesite putanju foldera koji zelite da obrisete(Folder\\\\Folder): ");
		try {
			String path = inputFromClient.readLine();
			inputForClient.println("\nUnesite naziv foldera koji zelite da obrisete: ");
			String stringZaBrisanje = inputFromClient.readLine();
			File folderZaBrisanje = new File(
					pathToFolder + "RegistrovaniKorisnici\\" + username + "\\" + path + "\\" + stringZaBrisanje);
			if (folderZaBrisanje.delete())
				inputForClient.println("USPESNO STE OBRISALI FAJL");
			else
				inputForClient.println("FOLDER NIJE PRAZAN");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void moveFile() {

		try {
			inputForClient.println("\nUnesite putanju do fajla/foldera koji zelite da premestite(Folder\\\\Folder): ");
			String sourcePath = inputFromClient.readLine();

			inputForClient.println("\nUnesite ime fajla/foldera koji zelite da premestite: ");
			String ime = inputFromClient.readLine();

			inputForClient.println(
					"\nUnesite putanju do novog foldera gde zelite da premestite file/folder (Folder\\\\Folder): ");
			String noviPath = inputFromClient.readLine();

			File sourceFile = new File(
					pathToFolder + "RegistrovaniKorisnici\\" + username + "\\" + sourcePath + "\\" + ime);
			File destFile = new File(
					pathToFolder + "RegistrovaniKorisnici\\" + username + "\\" + noviPath + "\\" + ime);

			if (sourceFile.renameTo(destFile)) {
				inputForClient.println("File moved successfully");
			} else {
				inputForClient.println("Failed to move file");
			}

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void renameDirs() {
		try {
			inputForClient.println("\nUnesite putanju foldera koji zelite da reimenujete(Folder\\\\Folder): ");
			String path = inputFromClient.readLine();

			inputForClient.println("\nUnesite naziv foldera koji zelite da reimenujete: ");
			String folderStari = inputFromClient.readLine();

			inputForClient.println("\nUnesite novo ime foldera: ");
			String folderNovi = inputFromClient.readLine();

			File sourceFile = new File(
					pathToFolder + "RegistrovaniKorisnici\\" + username + "\\" + path + "\\" + folderStari);
			File destFile = new File(
					pathToFolder + "RegistrovaniKorisnici\\" + username + "\\" + path + "\\" + folderNovi);

			if (sourceFile.renameTo(destFile)) {
				inputForClient.println("File renamed successfully");
			} else {
				inputForClient.println("Failed to rename file");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createDirs() {
		String folderPath;
		inputForClient.println("\nUnesite putanju gde zelite da kreirate novi folder: ");
		try {
			folderPath = inputFromClient.readLine();
			inputForClient.println("\nUnesite ime novog foldera: ");
			String folderName = inputFromClient.readLine();
			if (new File(pathToFolder + "RegistrovaniKorisnici\\" + username + "\\" + folderPath + "\\" + folderName)
					.mkdir()) {
				inputForClient.println("Uspesno ste napravili " + "" + folderName + " folder");
			} else
				inputForClient.println("Putanja nije dobra!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println("LOL");
		}

	}

	// https://www.geeksforgeeks.org/java-program-list-files-directory-nested-sub-directories-recursive-approach/

	private void listFiles(BufferedReader inputFromClient, PrintStream inputForClient, File[] arr, int index,
			int level) {

		// terminate condition
		if (index == arr.length)
			return;

		// tabs for internal levels
		for (int i = 0; i < level; i++)
			inputForClient.print("\t");

		// for files
		if (arr[index].isFile())
			inputForClient.println(arr[index].getName());

		// for sub-directories
		else if (arr[index].isDirectory()) {
			inputForClient.println("[" + arr[index].getName() + "]");

			// recursion for sub-directories
			listFiles(inputFromClient, inputForClient, arr[index].listFiles(), 0, level + 1);
		}

		// recursion for main directory
		listFiles(inputFromClient, inputForClient, arr, ++index, level);
	}

	private void uploadFile(BufferedReader inputFromClient, PrintStream inputForClient) {
		// srediti
		try {
			new File(pathToFolder + "RegistrovaniKorisnici\\" + username + "\\" + username + "DISK" + "\\" + "liki")
					.createNewFile();
		} catch (IOException e) {
			System.out.println("nije kreirao fajl");
		}
	}

	/* KRIPTOVANJE */
	static int result = -1;

	public static int kriptovanje(int base, int exponent, int modulus) {
		if ((base < 1) || (exponent < 0) || (modulus < 1)) {
			System.out.println("invalid");
		}
		result = 1;
		while (exponent > 0) {
			if ((exponent % 2) == 1) {
				result = (result * base) % modulus;
			}
			base = (base * base) % modulus;
			exponent = (int) Math.floor(exponent / 2);
		}
		return result;
	}

	public static int deKriptovanje(int base, int exponent, int modulus) {
		if ((base < 1) || (exponent < 0) || (modulus < 1)) {
			System.out.println("invalid");
		}
		result = 1;
		while (exponent > 0) {
			if ((exponent % 2) == 1) {
				result = (result * base) % modulus;
			}
			base = (base * base) % modulus;
			exponent = (int) Math.floor(exponent / 2);
		}
		return result;
	}

	public static int extractNumber(final String str) {

		if (str == null || str.isEmpty())
			System.out.println("Empty string.");

		StringBuilder sb = new StringBuilder();
		boolean found = false;
		for (char c : str.toCharArray()) {
			if (Character.isDigit(c)) {
				sb.append(c);
				found = true;
			} else if (found) {
				// If we already found a digit before and this char is not a digit, stop looping
				break;
			}
		}

		String broj = sb.toString();
		return Integer.parseInt(broj);
	}

	/* KRAJ KRIPTOVANJA */

	private void register(BufferedReader inputFromClient, PrintStream inputForClient) throws Exception {
		String username;
		String password;
		boolean usernameExists = false;

		boolean isValid = false;

		inputForClient.println("*** REGISTER ***");

		do {
			inputForClient.println("\nEnter Username:");
			username = inputFromClient.readLine();
			usernameExists = false;

			BufferedReader br = new BufferedReader(new FileReader(pathToFolder + "RegistrovaniKorisnici\\podaci.txt"));
			String line = br.readLine();
			while ((line != null)) {
				String[] info = line.split(",");
				String username1 = info[0];
				boolean promeniPrem = Boolean.parseBoolean(info[2]);

				if (username1.equals(username)) {
					prem = promeniPrem;
					usernameExists = true;
					break;
				}

				line = br.readLine();
			}

			br.close();

			if (usernameExists) {
				inputForClient.println("\nUsername already exist.");
			} else {
				isValid = true;
			}
		} while (!isValid);

		this.username = username;

		isValid = false;

		do {
			inputForClient.println("\n[PASSWORD MUST INCLUDE MAXIMUM TWO NUMBERS]Enter Password:");
			password = inputFromClient.readLine();
			if (!stringContainsNumber(password)) {
				inputForClient.println("\nPassword must include numbers and max is two numbers! Try again...");

			} else {
				if (provera(password) > 2)
					inputForClient.println("\nPassword must include numbers and max is two numbers! Try again...");
			}

		} while (!stringContainsNumber(password) || provera(password) > 2);

		while (true) {
			inputForClient.println("Da li zelite da imate premium mogucnosti(10$ per month)?[Da/Ne]");

			String premium = inputFromClient.readLine();

			if (premium.equalsIgnoreCase("da")) {
				prem = true;
				break;
			} else if (premium.equalsIgnoreCase("ne")) {
				prem = false;
				break;
			} else {
				inputForClient.println("[DA/NE] format!");
			}
		}

		inputForClient.println(
				"\nREGISTERED SUCCESSFULLY WITH:" + "\nUsername: " + username + " | Password: " + password + "\n");
		
		this.brojac = 0;

		String sharedLinkic = generateLink();

		// dodajemo direktorijum
		new File(pathToFolder + "RegistrovaniKorisnici\\" + username).mkdir();

		try {
			String filename = pathToFolder + "RegistrovaniKorisnici\\podaci.txt";
			FileWriter fw = new FileWriter(filename, true); // the true will append the new data
			fw.write(username + "," + password + "," + prem + "," + sharedLinkic + "\n");// appends the string to the
																							// file
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}

		loggedInMenu(inputFromClient, inputForClient);
	}

	public int provera(String sttr) {
		int count = 0;
		char[] ch = sttr.toCharArray();
		// Counts each character except space
		for (int i = 0; i < sttr.length(); i++) {
			if (Character.isDigit(ch[i]))
				count++;
		}
		return count;
	}

	public static boolean stringContainsNumber(String s) {
		return Pattern.compile("[0-9]").matcher(s).find();
	}

	private void login(BufferedReader inputFromClient, PrintStream inputForClient) throws Exception {
		String username;
		String password;
		String dbPassword = null;
		boolean dbPrem = false;
		boolean isValid = false;
		boolean usernameExists = false;

		inputForClient.println("\n*** LOGIN ***");

		do {
			inputForClient.println("\nEnter Username:");
			username = inputFromClient.readLine();
			usernameExists = false;

			BufferedReader br = new BufferedReader(new FileReader(pathToFolder + "RegistrovaniKorisnici\\podaci.txt"));
			String line = br.readLine();
			while ((line != null)) {
				String[] info = line.split(",");
				String username1 = info[0];
				String password1 = info[1];
				boolean promeniPrem = Boolean.parseBoolean(info[2]);

				if (username1.equals(username)) {
					dbPassword = password1;
					dbPrem = promeniPrem;
					usernameExists = true;
					break;
				}

				line = br.readLine();
			}

			br.close();

			if (usernameExists) {
				isValid = true;
			} else {
				inputForClient.println("\nUsername doesn't exist.");
			}
		} while (!isValid);

		this.username = username;

		isValid = false;

		do {
			inputForClient.println("\nEnter Password:");
			password = inputFromClient.readLine();

			if (dbPassword != null && dbPassword.equals(password)) {
				isValid = true;
			} else {
				inputForClient.println("\nWrong password.");
			}
		} while (!isValid);

		this.password = password;

		this.prem = dbPrem;
		this.brojac = 0;

		inputForClient.println("\nLOGGED IN SUCCESSFULLY!");
		loggedInMenu(inputFromClient, inputForClient);
	}

	private void logout(BufferedReader inputFromClient, PrintStream inputForClient) throws Exception {
		this.username = null;
		this.password = null;
		this.prem = false;
		this.brojac = 0;
		inputForClient.println("\nLOGGED OUT SUCCESSFULLY!\n");
		menu(inputFromClient, inputForClient);
	}

	private void closeCommunication(BufferedReader inputFromClient, PrintStream inputForClient) {

		try {
			inputForClient.println(">>> Exited successful!");
			socketForCommunication.close();
		} catch (IOException e) {
			System.err.println("GRESKA PRILIKOM ZATVARANJA KOMUNIKACIJE.");
		}
	}

	public void run() {

		try {

			inputFromClient = new BufferedReader(new InputStreamReader(socketForCommunication.getInputStream()));
			inputForClient = new PrintStream(socketForCommunication.getOutputStream());

			menu(inputFromClient, inputForClient);

		} catch (Exception e) {
			System.err.println("GRESKA PRILIKOM POKRETANJA RUN METODE.");
		}
	}
}