package enchantmentChanges;

public class unbreaking {
    double unbreakingModifier;

    public void setUnbreakingModifier(int level) {
        this.unbreakingModifier = (level*0.5)+1.0;
    }
}
