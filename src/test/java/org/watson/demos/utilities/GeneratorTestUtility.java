package org.watson.demos.utilities;

import lombok.experimental.UtilityClass;
import org.watson.demos.GreetingRequest;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class GeneratorTestUtility {

    private static final Locale[] AVAILABLE_LOCALES = Locale.getAvailableLocales();
    private static final int CONTENT_COUNT = 10;

    public static List<GreetingRequest> generateGreetingRequests(final String content) {
        return IntStream.range(0, CONTENT_COUNT).boxed()
                .map(i -> GreetingRequest.newBuilder()
                        .setLocale(AVAILABLE_LOCALES[i % AVAILABLE_LOCALES.length].toString())
                        .setContent(content + " " + i))
                .map(GreetingRequest.Builder::build)
                .collect(Collectors.toUnmodifiableList());
    }
}
