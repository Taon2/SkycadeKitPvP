package net.skycade.kitpvp.coreclasses.option;

import java.util.function.Function;
import java.util.function.Supplier;

public class Some<T> implements IOption<T> {

    private T value;

    public Some(T value) {
        this.value = value;
    }

    public <U> U visit(Supplier<U> onNone, Function<T, U> onSome) {
        return onSome.apply(value);
    }

}
