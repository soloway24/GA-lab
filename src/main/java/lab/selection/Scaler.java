package lab.selection;

public interface Scaler {

    <T extends Number> double scale(T fitness, SelectionContext selectionContext);

}