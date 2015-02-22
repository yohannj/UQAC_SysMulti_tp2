import java.util.List;


public class Regle {
    
    private List<String> premisses;
    private List<String> consequences;

    public Regle() {
        
    }
    
    public boolean satisfaitCondition(String fait) {
        //TODO permet de savoir si un fait correspond à un prémisse d’une règle.
        return false;
    }
    
    public boolean satisfaitConditions(List<String> faits) {
        //TODO permet de savoir si un ensemble de faits satisfait tous les prémisses d’une règle.
        return false;
    }
}
