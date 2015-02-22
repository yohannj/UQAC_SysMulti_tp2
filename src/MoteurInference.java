import java.io.*;
import java.util.*;

public class MoteurInference {

	private static MoteurInference MI;
	private static final String filename = "Battleship.rules";
	private List<Regle> regles;
	private List<String> faits;
	private char[][] carte;

	public static MoteurInference getInstance() {
		if (MI == null) {
			MI = new MoteurInference();
		}
		return MI;
	}

	/**
	 * Constructeur, instanciue la liste de règles à partir du fichier
	 * Battleship.rules
	 */
	private MoteurInference() {
		//TODO instancier les règles à partir du fichier Battleship.rules
		regles = parser(filename);
	}

	/**
	 * @param fait
	 * @return
	 *         Vérifie si un fait est solution, c.-à-d. qu’il ne fait pas partie
	 *         des faits connus.
	 */
	public boolean estSolution(String fait) {
		for (String s : faits) {
			if (fait.equals(s))
				return false;
		}
		return true;
	}

	public int[] calculCoup() {
		//TODO contient toute la logique de décision du moteur d’inférence.
		/*
		 * Il utilise un chainage avant simple pour décider quel sera le coup
		 * suivant à réaliser en
		 * fonction des faits de base contenu dans la base de faits du moteur
		 * d’inférence. Cette
		 * fonction est aussi capable de revenir un coup en arrière si l’avant
		 * dernier coup a été
		 * une réussite et si le dernier coup a été un échec. Le chainage avant
		 * simple consiste à
		 * parcourir l’ensemble des faits initiaux et voir si pour un fait donné
		 * il existe une règle
		 * dans le cas où le fait n’est déjà pas une solution. Si le fait donné
		 * est une solution alors
		 * il est ajouté dans la base de faits courante du moteur. Une fois
		 * l’ensemble des faits
		 * parcourus, la base de faits du moteur contient alors le fait lui
		 * permettant de prendre la
		 * décision.
		 */
		return null;
	}

	/**
	 * @param carte
	 *            Mets à jour les informations ocnnus sur la carte
	 */
	public void majCarte(char[][] carte) {
		this.carte = carte;
	}

	private void appliquerRegle(Regle r) {
		//TODO maj faits
	}

	private List<Regle> parser(String filename) {
		File f = new File(filename);
		List<Regle> liste = new ArrayList<>();
		try {
			InputStream ips = new FileInputStream(f);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne = br.readLine()) != null) {
				//on vérifie que la ligne ne soit pas un commentaire
				if (ligne.charAt(0) != '#') {
					Regle regle = new Regle();
					String[] ligneSpliter = ligne.split("=>");
					String listeFait = ligneSpliter[0].replace(" ","");
					if (listeFait.contains(",")) {
						String[] splitFait = listeFait.split(",");
						for (int i = 0; i < splitFait.length; i++) {
							regle.addPremisse(splitFait[i]);
						}
					}else{
						regle.addPremisse(listeFait);
					}
					String listeConseq = ligneSpliter[1].replace(" ","");
					if (listeConseq.contains(",")) {
						String[] splitConseq = listeConseq.split(",");
						for (int i = 0; i < splitConseq.length; i++) {
							regle.addConsequence(splitConseq[i]);
						}
					}else{
						regle.addConsequence(listeConseq);
					}
					liste.add(regle);
				}
			}
			br.close();
			return liste;

		} catch (Exception e) {
			System.out.println(e);
		}

		return liste;
	}
}
