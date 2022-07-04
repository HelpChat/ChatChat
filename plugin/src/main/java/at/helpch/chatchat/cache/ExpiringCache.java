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

            // In the very unlikely event that nanos is 0, set it to 1;
            // no one will notice 1 ns of tardiness.
            this.expirationNanos = nanos == 0 ? 1 : nanos;
        }
    }

    public @NotNull Optional<T> get() {
        long nanos = this.expirationNanos;
        long now = System.nanoTime();

        if (nanos == 0 || now - nanos >= 0) {
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
}