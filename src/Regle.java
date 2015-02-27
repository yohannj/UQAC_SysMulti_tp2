import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Regle {

    private List<String> premisses;
    private List<String> consequences;

    public Regle() {
        premisses = new ArrayList<String>();
        consequences = new ArrayList<String>();
    }

    public Regle(Regle r, int dernierX, int dernierY) {
        premisses = new ArrayList<String>(r.premisses);
        consequences = new ArrayList<String>(r.consequences);
        adapteAuDernierPoint(dernierX, dernierY);
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
                return true;
        }
        return false;
    }

    /**
     * @param faits
     * @return 
     * Permet de savoir si un ensemble de faits satisfait tous les prémisses d’une règle. 
     */
    public boolean satisfaitConditions(Set<String> faits) {
        for (String s : premisses) {
            if (!faits.contains(s))
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
            premisses.set(i, premisses.get(i).replace("dernierX+1", ""
                    + (dernierX + 1)));
            premisses.set(i, premisses.get(i).replace("dernierY+1", ""
                    + (dernierY + 1)));
            premisses.set(i, premisses.get(i).replace("dernierX-1", ""
                    + (dernierX - 1)));
            premisses.set(i, premisses.get(i).replace("dernierY-1", ""
                    + (dernierY - 1)));
            premisses.set(i, premisses.get(i)
                    .replace("dernierX", "" + dernierX));
            premisses.set(i, premisses.get(i)
                    .replace("dernierY", "" + dernierY));
        }
        for (int i = 0; i < consequences.size(); ++i) {
            consequences.set(i, consequences.get(i).replace("dernierX+1", ""
                    + (dernierX + 1)));
            consequences.set(i, consequences.get(i).replace("dernierY+1", ""
                    + (dernierY + 1)));
            consequences.set(i, consequences.get(i).replace("dernierX-1", ""
                    + (dernierX - 1)));
            consequences.set(i, consequences.get(i).replace("dernierY-1", ""
                    + (dernierY - 1)));
            consequences.set(i, consequences.get(i)
                    .replace("dernierX", "" + dernierX));
            consequences.set(i, consequences.get(i)
                    .replace("dernierY", "" + dernierY));
        }
    }

    public String toString() {
        String res = "";
        for(int i = 0; i < premisses.size(); ++i) {
            res += premisses.get(i) + " ";
        }
        res += "=>";
        for(int i = 0; i < consequences.size(); ++i) {
            res += consequences.get(i) + " ";
        }
        
        return res;
    }
}
