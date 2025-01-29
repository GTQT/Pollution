package keqing.pollution.api.capability;

public interface IVisHatch {
    int getTier();
    int getVisStore();
    int getMaxVisStore();
    boolean drainVis(int amount,boolean simulate);
}