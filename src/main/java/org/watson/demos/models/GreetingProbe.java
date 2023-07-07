package org.watson.demos.models;

import lombok.Builder;

import java.util.Locale;

@lombok.Value
@Builder
public class GreetingProbe {
    Locale locale;
}
