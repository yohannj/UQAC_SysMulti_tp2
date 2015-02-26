import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class Regle {

    private List<String> premisses;
    private List<String> consequences;

    public Regle() {
        premisses = new ArrayList<String>();
        consequences = new ArrayList<String>();
    }

    public Regle(Regle r) {
        premisses = new ArrayList<String>(r.premisses);
        consequences = new ArrayList<String>(r.consequences);
    }

    public void addPremisse(String fait) {
        premisses.add(fait);
    }

    public void addConsequence(String conseq) {
        consequences.add(conseq);
    }

    public List<String> getConsequence() {
        return consequences;
    }

    /**
     * @param fait
     * @return
     * Permet de savoir si un fait correspond à un prémisse d’une règle.
     */
    public boolean satisfaitCondition(String fait) {
        for (String s : premisses) {
            if (fait.equals(s))
                return false;
        }
        return true;
    }

    /**
     * @param faits
     * @return null si non satisfait. Sinon le Point(x;y) validant la règle, le Point n'est pas instancié s'il n'est pas nécessaire pour la lecture de la règle
     */
    public Point satisfaitConditions(Set<String> faits) {
        Map<Point, Integer> nbPremissesValideesPar = new HashMap<Point, Integer>();

        for (String s : premisses) {
            for (Iterator<String> i = faits.iterator(); i.hasNext();) {
                String fait = i.next();
                List<Object> param = récupérerParamètreDuFait(fait);
                if (s.equals(i.next())) {
                    
                }
            }
            //if (!satisfied)
              //  return null;
        }
        return null;
    }

    /**
     * @param dernierX
     * @param dernierY
     * Remplace toutes les occurences de "dernierX" et "dernierY" par leurs valeurs 
     */
    public void adapteAuDernierPoint(int dernierX, int dernierY) {
        for (int i = 0; i < premisses.size(); ++i) {
            premisses.set(i, premisses.get(i).replaceAll("dernierX", ""
                    + dernierX));
            premisses.set(i, premisses.get(i).replaceAll("dernierY", ""
                    + dernierY));
        }
        for (int i = 0; i < premisses.size(); ++i) {
            consequences.set(i, consequences.get(i).replaceAll("dernierX", ""
                    + dernierX));
            consequences.set(i, consequences.get(i).replaceAll("dernierY", ""
                    + dernierY));
        }
    }

    private List<Object> récupérerParamètreDuFait(String fait) {
        List<Object> res = new ArrayList<Object>();
        String[] faitSplit = fait.split(";");
        int x = calcString(faitSplit[0].split("(")[1]);
        int y;
        String t = null;
        if (faitSplit.length == 2) {
            y = calcString(faitSplit[1].split(")")[0]);
        } else {
            y = calcString(faitSplit[1]);
            t = faitSplit[2].split(")")[0];
        }
        res.add(x);
        res.add(y);
        res.add(t);
        return res;
    }

    private int calcString(String s) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        try {
            return (int) engine.eval(s);
        } catch (ScriptException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
