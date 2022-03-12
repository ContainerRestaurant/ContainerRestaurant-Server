package container.restaurant.server.utils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Random;

public class RandomPicker<E> {

    private final Random random;
    private List<E> list;

    public RandomPicker(List<E> list) {
        this.list = list;
        this.random = new Random();
    }

    public RandomPicker(List<E> list, Random random) {
        this.list = list;
        this.random = random;
    }

    public E pick() {
        if (list.size() == 0)
            return null;
        return list.get(random.nextInt(list.size()));
    }

    public RandomPicker<E> update(@NotNull List<E> list) {
        this.list = list;
        return this;
    }

    public int size() {
        return list.size();
    }

}
