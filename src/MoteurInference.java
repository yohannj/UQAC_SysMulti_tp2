import java.awt.Point;
import java.io.*;
import java.util.*;

public class MoteurInference {

    private static MoteurInference MI;
    private final String FILE_NAME = "src/Battleship.rules";
    private final String FILE_NAME_inter = "src/inter.rules";
    private List<Regle> regles;
    private Set<String> faits;
    private char[][] carte;

    private Point dernier_coup;
    private Point avant_dernier_coup;
    private boolean termine;
    private boolean coup_aleatoire;

    public static MoteurInference getInstance() {
        if (MI == null) {
            MI = new MoteurInference();
        }
        return MI;
    }

    /**
     * Constructeur, instancie la liste de règles à partir du fichier
     * Battleship.rules
     */
    private MoteurInference() {
        dernier_coup = new Point(-1, -1);
        avant_dernier_coup = new Point(-1, -1);
        regles = parser(FILE_NAME);
        faits = new HashSet<String>();
    }

    /**
     * @param fait
     * @return Si un fait est solution (il ne fait pas partie des faits connus),
     *         true
     */
    public boolean estSolution(String fait) {
        return !faits.contains(fait);
    }

    /**
     * Chaînage avant
     * 
     * @return Tableau contenant 2 int, les coordonnées du prochain coup à
     *         jouer : {x,y}
     */
    public int[] calculCoup() {
        
        //0. Initialise variable perso
        termine = false;
        coup_aleatoire = false;
        
        //1. Obtenir les faits initiaux
        initFaits();

        List<Regle> regles_non_marquees = new ArrayList<Regle>(); //Liste contenant une copie des règles
        for (int i = 0; i < regles.size(); ++i) {
            regles_non_marquees.add(new Regle(regles.get(i), dernier_coup.x, dernier_coup.y));
        }

        if (dernier_coup.x > -1) {
            File f = new File(FILE_NAME_inter);
            FileWriter sortie;
            try {
                sortie = new FileWriter(f);

                for (int i = 0; i < regles_non_marquees.size(); i++) {
                    sortie.write(regles_non_marquees.get(i).toString()
                            + String.format("%n"));
                }
                sortie.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //2. Tant que (Pas terminé et il reste au moins une règle non marquée) faire
        while (!termine && !regles_non_marquees.isEmpty()) {
            List<Regle> regle_applicable = new ArrayList<Regle>();

            //2.1 Sélectionner les règles applicables : celles non marquées
            for (Iterator<Regle> i = regles_non_marquees.iterator(); regle_applicable.isEmpty() && i.hasNext();) {
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
            System.out.println(regle_appliquee.toString());
            appliquerRegle(regle_appliquee);

            //2.4 Marquer la règle
            regles_non_marquees.remove(regle_appliquee);

        }

        //3. Fin
        avant_dernier_coup = dernier_coup;
        dernier_coup = null;
        for (Iterator<String> i = faits.iterator(); i.hasNext()
                && dernier_coup == null;) {
            String fait = i.next();
            if (fait.contains("jouer")) {
                System.out.println(fait);
                int x = Integer.parseInt(fait.split(";")[0].split("\\(")[1]);
                int y = Integer.parseInt(fait.split(";")[1].split("\\)")[0]);
                dernier_coup = new Point(x, y);
            }
        }
        
        avant_dernier_coup = coup_aleatoire ? dernier_coup : avant_dernier_coup;

        return new int[] { dernier_coup.x, dernier_coup.y };
    }

    /**
     * @param carte
     *            Mets à jour les informations connus sur la carte
     */
    public void majCarteEtDernierPoint(char[][] carte) {
        System.out.println("Début : " + avant_dernier_coup.x + " - " + avant_dernier_coup.y + " - "+ dernier_coup.x + " - "+ dernier_coup.y);
        this.carte = carte;
        if (avant_dernier_coup.x > -1 && dernier_coup.x > -1
                && carte[avant_dernier_coup.x][avant_dernier_coup.y] != 'o'
                && carte[avant_dernier_coup.x][avant_dernier_coup.y] != 'v'
                && carte[dernier_coup.x][dernier_coup.y] != carte[avant_dernier_coup.x][avant_dernier_coup.y]) {
            dernier_coup = avant_dernier_coup;
        }
        System.out.println("Fin : " + avant_dernier_coup.x + " - " + avant_dernier_coup.y + " - "+ dernier_coup.x + " - "+ dernier_coup.y);
    }

    private void appliquerRegle(Regle r) {
        int x_cible, y_cible;
        int i = 1;
        boolean trouv = false;
        for (String s : r.getConsequence()) {
            if (s.contains("jouer")) {
                termine = true;
                if (!s.contains(";")) {
                    if (s.contains("jouer(dernier")) {
                        System.out.println("I SHOULD NOT BE HERE");
                        //Si on demande de jouer par rapport au dernier coup
                        x_cible = valeur_dep_x(s.split(";")[0]);
                        y_cible = valeur_dep_y(s.split(";")[1]);
                        faits.add("jouer(" + x_cible + ";" + y_cible);
                    } else if (s.contains("jouer(last")) {
                        System.out.println("I will play last");
                        //on demande à jouer le last machin
                        if (s.contains("gauche")) {
                            while (!trouv) {
                                if (carte[dernier_coup.x][dernier_coup.y - i] == 'v') {
                                    y_cible = dernier_coup.y - i;
                                    trouv = true;
                                    faits.add("jouer(" + dernier_coup.x + ";"
                                            + y_cible + ")");
                                } else {
                                    i++;
                                }
                            }
                        } else if (s.contains("bas")) {
                            while (!trouv) {
                                if (carte[dernier_coup.x + i][dernier_coup.y] == 'v') {
                                    x_cible = dernier_coup.x + i;
                                    trouv = true;
                                    faits.add("jouer(" + x_cible + ";"
                                            + dernier_coup.y + ")");
                                } else {
                                    i++;
                                }
                            }
                        } else if (s.contains("haut")) {
                            while (!trouv) {
                                if (carte[dernier_coup.x - i][dernier_coup.y] == 'v') {
                                    x_cible = dernier_coup.x - i;
                                    trouv = true;
                                    faits.add("jouer(" + x_cible + ";"
                                            + dernier_coup.y + ")");
                                } else {
                                    i++;
                                }
                            }
                        }else if (s.contains("droite")) {
                            while (!trouv) {
                                if (carte[dernier_coup.x ][dernier_coup.y+i] == 'v') {
                                    y_cible = dernier_coup.y + i;
                                    trouv = true;
                                    faits.add("jouer(" + dernier_coup.x + ";"
                                            + y_cible + ")");
                                } else {
                                    i++;
                                }
                            }
                        }

                    } else {// traiter le code jouer(aleatoire)
                        coup_aleatoire = true;
                        Iterator<String> iterator = faits.iterator();
                        while (iterator.hasNext() && !trouv) {
                            String element = (String) iterator.next();
                            if (element.matches("inconnu(.*;.*)")) {
                                trouv = true;
                                x_cible = Integer.parseInt(element.substring(8, element.length() - 1)
                                        .split(";")[0]);
                                y_cible = Integer.parseInt(element.split(";")[1].replace(")", ""));
                                faits.add("jouer(" + x_cible + ";" + y_cible
                                        + ")");
                            }

                        }
                    }
                } else {
                    faits.add(s);
                }
            } else {
                faits.add(s);
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
        //Devrais fonctionner en utilisant uniquement false
        return false;
    }

    private List<Regle> parser(String filename) {
        File f = new File(filename);
        List<Regle> liste = new ArrayList<Regle>();
        try {
            InputStream ips = new FileInputStream(f);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            while ((ligne = br.readLine()) != null) {
                //on vérifie que la ligne ne soit pas un commentaire
                if (!ligne.contains("#")) {
                    Regle regle = new Regle();
                    String[] ligneSpliter = ligne.split("=>");
                    String listeFait = ligneSpliter[0].replace(" ", "");
                    for (String s : listeFait.split(",")) {
                        if (!s.equals("")) //Ajouter pour permettre un coup aléatoire
                            regle.addPremisse(s);
                    }
                    if (ligneSpliter.length == 1) {
                        System.out.println(ligne);
                        System.out.println(ligne.contains("#"));
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
            e.printStackTrace();
        }

        return liste;
    }

    private int valeur_dep_x(String chaine) {
        //Rappel : "Conseil : Utiliser javax.script.ScriptEngineManager et javax.script.ScriptEngine. (Attention ? ils renvoient un double, peut-être faire le passage en int) -> http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form"
        if (chaine.contains("+")) {
            int dep = Integer.parseInt(chaine.split("+")[1]);
            return dernier_coup.x + dep;
        } else if (chaine.contains("-")) {
            int dep = Integer.parseInt(chaine.split("-")[1]);
            return dernier_coup.x - dep;
        }
        return 0;
    }

    private int valeur_dep_y(String chaine) {
        //Rappel : "Conseil : Utiliser javax.script.ScriptEngineManager et javax.script.ScriptEngine. (Attention ? ils renvoient un double, peut-être faire le passage en int) -> http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form"
        if (chaine.contains("+")) {
            int dep = Integer.parseInt(chaine.split("+")[1]);
            return dernier_coup.y + dep;
        } else if (chaine.contains("-")) {
            int dep = Integer.parseInt(chaine.split("-")[1]);
            return dernier_coup.y - dep;
        }
        return 0;
    }
}
