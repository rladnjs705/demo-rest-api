package com.example.demorestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RunWith(JUnitParamsRunner.class)
class EventTest {

    @Test
    void builder() {
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    void javaBean() {
        //Given
        String name = "Event";
        String description = "Spring";

        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @ParameterizedTest(name = "{index} => basePrice={0}, maxPrice={1}, isFree={2}")
    @CsvSource({
            "0, 0, true",
            "100, 0, falae",
            "0, 1000, falae"
    })
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isEqualTo(isFree);

    }

    @ParameterizedTest(name = "{index} => basePrice={0}, maxPrice={1}, isFree={2}")
    @MethodSource("testFree2Params")
    public void testFree2(int basePrice, int maxPrice, boolean isFree) {
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isEqualTo(isFree);

    }

    // static 있어야 동작
    private static Object[] testFree2Params() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false},
                new Object[]{100, 200, false}
        };
    }

    // 외국 문서들에서는 스트림을 활용하는 경우가 많았다.
/*
    private static Stream<Arguments> sumProvider() {
        return Stream.of(
                Arguments.of(1, 1, 2),
                Arguments.of(2, 3, 5)
        );
    }
*/

    @ParameterizedTest(name = "{index} => location={0}, isOffline={1}")
    @MethodSource("testOfflineParams")
    public void testOffline(String location, boolean isOffline) {
        //Given
        Event event = Event.builder()
                .location(location)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private static Object[] testOfflineParams() {
        return new Object[]{
                new Object[]{"강남", true},
                new Object[]{null, false},
                new Object[]{"     ", false}
        };
    }

}