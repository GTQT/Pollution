package meowmel.pollution.api.capability;

/** Holds the active tarot card used as a non-consumable recipe authorization. */
public interface ITarotHatch {

    String getActiveTarot();

    boolean hasTarot(String tarotId);

    void setFocusLocked(boolean locked);

    boolean isFocusLocked();
}
