import java.awt.Point;
import java.io.*;
import java.util.*;

public class MoteurInference {

    private static MoteurInference MI;
    private final String FILE_NAME = "src/Battleship.rules";
    private List<Regle> regles;
    private Set<String> faits;
    private char[][] carte;

    private Point dernier_coup;
    private Point avant_dernier_coup;
    private boolean termine;

    public static MoteurInference getInstance() {
        if (MI == null) {
            MI = new MoteurInference();
        }
        return MI;
    }

    /**
     * Constructeur, instancie la liste de règles à partir du fichier Battleship.rules
     */
    private MoteurInference() {
        dernier_coup = new Point(-1, -1);
        regles = parser(FILE_NAME);
        faits = new HashSet<String>();
    }

    /**
     * @param fait
     * @return Si un fait est solution (il ne fait pas partie des faits connus), true
     */
    public boolean estSolution(String fait) {
        return !faits.contains(fait);
    }

    /**
     * Chaînage avant
     * @return Tableau contenant 2 int, les coordonnées du prochain coup à jouer : {x,y} 
     */
    public int[] calculCoup() {
        //1. Obtenir les faits initiaux
        initFaits();

        List<Regle> regles_non_marquees = new ArrayList<Regle>(regles);
        regles_non_marquees.parallelStream()
                .forEach(r -> r.adapteAuDernierPoint(dernier_coup.x, dernier_coup.y));
        termine = false;

        //2. Tant que (Pas terminé et il reste au moins une règle non marquée) faire
        while (!termine && !regles_non_marquees.isEmpty()) {
            List<Regle> regle_applicable = new ArrayList<Regle>();

            //2.1 Sélectionner les règles applicables : celles non marquées
            for (Iterator<Regle> i = regles_non_marquees.iterator(); i.hasNext();) {
                Regle r = i.next();

                //2.1 Sélectionner les règles applicables : si une des règles est en contradiction, marquer la règle
                if (enContradiction(r)) {
                    regles_non_marquees.remove(r);
                } else {

                    //2.1 Sélectionner les règles applicables : celles dont les conditions existent dans la base de faits
                    if (r.satisfaitConditions(faits)) {
                        regle_applicable.add(r);
                    }
                }
            }

            //2.2 Choisir la règle à appliquer (arbitrairement, ou autre)
            Regle regle_appliquee = regle_applicable.get(0);

            //2.3 Appliquer la règle: ajouter les conclusions à la base de faits
            appliquerRegle(regle_appliquee);

            //2.4 Marquer la règle
            regles_non_marquees.remove(regle_appliquee);

        }

        //3. Fin
        dernier_coup = null;
        for (Iterator<String> i = faits.iterator(); i.hasNext()
                && dernier_coup == null;) {
            String fait = i.next();
            if (fait.contains("jouer")) {
                int x = Integer.parseInt(fait.split(";")[0].split("(")[1]);
                int y = Integer.parseInt(fait.split(";")[1].split(")")[0]);
                avant_dernier_coup = dernier_coup;
                dernier_coup = new Point(x, y);
            }
        }

        //FIXME remove this if once MoteurInference is functionnal
        if (dernier_coup == null) {
            dernier_coup = new Point(0, 0);
        }

        return new int[] { dernier_coup.x, dernier_coup.y };
    }

    /**
     * @param carte
     * Mets à jour les informations connus sur la carte
     */
    public void majCarteEtDernierPoint(char[][] carte) {
        this.carte = carte;
        if(carte[dernier_coup.x][dernier_coup.y] == 'o' && carte[avant_dernier_coup.x][avant_dernier_coup.y] != 'o' && carte[avant_dernier_coup.x][avant_dernier_coup.y] != 'v') {
            dernier_coup = avant_dernier_coup;
        }
    }

    private void appliquerRegle(Regle r) { //Conseil : Utiliser javax.script.ScriptEngineManager et javax.script.ScriptEngine. (Attention ? ils renvoient un double, peut-être faire le passage en int) -> http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
        for (String s : r.getConsequence()) {
            if (s.contains("jouer")) {
                termine = true;
                if (s.matches("jouer(.*;.*)")) {
                    //TODO Ajouter le fait jouer(x,y) dans les faits. (Il ne faut plus de calcul dedans)
                } else if (s.contains("last")) {
                    //TODO On est dans le cas jouer(last_gauche) || jouer(last_haut) || jouer(last_droite). Déterminer (à l'aide d'une nouvelle fonction ?) x,y pour pouvoir ajouter jouer(x,y) dans les faits.
                } else {
                    //TODO On est dans le cas jouer(aleatoire). Déterminer x,y depuis un fait "inconnu(x,y)" pour pouvoir ajouter jouer(x,y) dans les faits.
                }
            } else {
                //TODO Rajout des conséquences dans les faits.
            }
        }
    }

    /**
     * Initialise la base de faits
     */
    private void initFaits() {
        faits = new HashSet<String>();

        for (int x = 0; x < carte.length; ++x) {
            for (int y = 0; y < carte[0].length; ++y) {
                faits.add("case(" + x + ";" + y + ")='" + carte[x][y] + "'");
            }
        }

    }

    /**
     * @param r
     * @return Si la règle est en contradiction avec les faits, true
     */
    private boolean enContradiction(Regle r) { //Conseil : Utiliser javax.script.ScriptEngineManager et javax.script.ScriptEngine. (Attention ? ils renvoient un double, peut-être faire le passage en int) -> http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
        //TODO Compléter en vérifiant les non_
        return false;
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
                    String listeFait = ligneSpliter[0].replace(" ", "");
                    for (String s : listeFait.split(",")) {
                        if (!s.equals("")) //Ajouter pour permettre un coup aléatoire
                            regle.addPremisse(s);
                    }
                    String listeConseq = ligneSpliter[1].replace(" ", "");
                    for (String s : listeConseq.split(",")) {
                        regle.addConsequence(s);
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
