package container.restaurant.server.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RandomPickerTest {

    @Test
    void pickTest() {
        //given
        List<Integer> list = List.of(0,1,2,3,4,5,6,7,8,9);
        int firstRandom = 3;
        int secondRandom = 7;
        int thirdRandom = 1;

        Random random = mock(Random.class);
        when(random.nextInt(anyInt()))
                .thenReturn(firstRandom, secondRandom, thirdRandom);

        RandomPicker<Integer> picker = new RandomPicker<>(list, random);

        //expect
        assertThat(picker.pick()).isEqualTo(list.get(firstRandom));
        assertThat(picker.pick()).isEqualTo(list.get(secondRandom));
        assertThat(picker.pick()).isEqualTo(list.get(thirdRandom));
    }

    @Test
    void updateTest() {
        //given
        List<Integer> list = List.of(0,1,2,3,4,5,6,7,8,9);
        int firstRandom = 3;
        int secondRandom = 7;
        int thirdRandom = 1;

        Random random = mock(Random.class);
        when(random.nextInt(anyInt()))
                .thenReturn(firstRandom, secondRandom, thirdRandom);

        RandomPicker<Integer> picker = new RandomPicker<>(null, random);
        picker.update(list);

        //expect
        assertThat(picker.pick()).isEqualTo(list.get(firstRandom));
        assertThat(picker.pick()).isEqualTo(list.get(secondRandom));
        assertThat(picker.pick()).isEqualTo(list.get(thirdRandom));
    }

}