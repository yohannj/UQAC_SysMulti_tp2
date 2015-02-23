import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Regle {

    private List<String> premisses;
    private List<String> consequences;

    public Regle() {
        premisses = new ArrayList<>();
        consequences = new ArrayList<>();
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
}
