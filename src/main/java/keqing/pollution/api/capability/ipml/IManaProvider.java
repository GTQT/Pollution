package keqing.pollution.api.capability.ipml;

public interface IManaProvider {

    IManaProvider getManaProvider();

    boolean drainMana(int amount,boolean sim);
}
