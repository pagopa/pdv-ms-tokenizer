package it.pagopa.pdv.tokenizer.core.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogUtils {

    public static final Marker CONFIDENTIAL_MARKER = MarkerFactory.getMarker("CONFIDENTIAL");

}
