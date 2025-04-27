package utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration duration) throws IOException {
        if (duration != null) {
            out.value(duration.getSeconds());
        } else {
            Duration.ofSeconds(0L);
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        long seconds = in.nextLong();
        return Duration.ofSeconds(seconds);
    }
}
