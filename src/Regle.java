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
     * @return 
     * Permet de savoir si un ensemble de faits satisfait tous les prémisses d’une règle. 
     */
    public boolean satisfaitConditions(Set<String> faits) {
        for (String s : premisses) {
            boolean satisfied = false;
            for (Iterator<String> i = faits.iterator(); !satisfied
                    && i.hasNext();) {
                if (s.equals(i.next()))
                    satisfied = true;
            }
            if (!satisfied)
                return false;
        }
        return true;
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

    private int calcString(String s) { //TODO utiliser dans adapteAuDernierPoint
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        try {
            return (int) engine.eval(s);
        } catch (ScriptException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
