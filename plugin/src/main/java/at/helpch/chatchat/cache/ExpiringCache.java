package at.helpch.chatchat.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * An expiring cache.
 *
 * @param <T> the cached type
 * @author Initially written by <a href="https://github.com/lucko/">lucko</a> for
 * <a href="https://github.com/LuckPerms/LuckPerms">LuckPerms</a>.
 */
public class ExpiringCache<T> {
    private final long durationNanos;

    private volatile T value;

    // when to expire. 0 means "not yet initialized".
    private volatile long expirationNanos;

    /**
     * Creates a new expiring cache. The cache will expire after the specified duration.
     * <br>
     * Use a negative duration to disable expiration.
     *
     * @param duration amount of time to keep the cached value in the cache
     * @param unit the {@link TimeUnit} of the duration
     */
    public ExpiringCache(long duration, TimeUnit unit) {
        this.durationNanos = unit.toNanos(duration);
    }

    public void put(T put) {
        final long now = System.nanoTime();

        synchronized (this) {
            // set the value
            if (put == null) {
                this.invalidate();
                return;
            }

            value = put;
            // reset expiration timer
            final var nanos = now + this.durationNanos;

            // In the very unlikely event that nanos is <= 0, set it to 1;
            // This can happen if duration <= -now.
            this.expirationNanos = nanos <= 0 ? 1 : nanos;
        }
    }

    public @NotNull Optional<T> get() {
        long nanos = this.expirationNanos;
        long now = System.nanoTime();

        // If value has not been initialized or cache is expiring and value has expired.
        if (nanos == 0 || (now - nanos >= 0 && durationNanos >= 0)) {
            synchronized (this) {
                if (nanos == this.expirationNanos) { // recheck for lost race
                    return Optional.empty();
                }
            }
        }
        return Optional.ofNullable(this.value);
    }

    public void invalidate() {
        this.expirationNanos = 0;
    }

    public boolean isPermanent() {
        return this.durationNanos < 0;
    }
}
