/*
 * Copyright 2022 Kat+ Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package plus.kat.kernel;

import plus.kat.anno.NotNull;

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.stream.*;

import java.io.IOException;

import static plus.kat.chain.Space.$;
import static plus.kat.chain.Space.$s;
import static plus.kat.chain.Space.$M;
import static plus.kat.chain.Space.$L;
import static plus.kat.kernel.Radar.uncork;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Mage implements Solver {
    /**
     * chain stream
     */
    protected final Alias alias;
    protected final Value value;

    /**
     * codec analyze
     */
    private long data = 0L;
    private long mask = 1L;
    private boolean mutable;

    /**
     * @param radar the specified {@code radar}
     */
    public Mage(
        @NotNull Radar radar
    ) {
        alias = radar.alias;
        value = radar.value;
    }

    /**
     * @param b1 the bucket of {@code alias}
     * @param b2 the bucket of {@code value}
     */
    public Mage(
        @NotNull Bucket b1,
        @NotNull Bucket b2
    ) {
        alias = new Alias(b1);
        value = new Value(b2);
    }

    /**
     * Reads json stream
     *
     * <pre>{@code
     *  {
     *     "uid": 1,
     *     "name": "kraity",
     *     "role": "developer",
     *     "blocked": 0,
     *     "resource": {
     *         "age": 6,
     *         "devote": 1024
     *     }
     *  }
     * }</pre>
     *
     * @param p specify the data transfer pipeline
     * @param r specify the source of decoded data
     * @throws IOException Unexpected errors by {@link Pipe} or {@link Reader}
     */
    @Override
    public void read(
        @NotNull Pipe p,
        @NotNull Reader r
    ) throws IOException {
        Boot:
        // decode json stream
        while (r.also()) {
            byte b = r.read();
            if (b <= 0x20) {
                switch (b) {
                    case 0x09:
                    case 0x0A:
                    case 0x0D:
                    case 0x20: {
                        continue;
                    }
                }
                throw new UnexpectedCrash(
                    "Unexpectedly, byte '" + b + "' <= 32"
                );
            }

            switch (b) {
                case '{': {
                    create(p, r, true);
                    break Boot;
                }
                case '[': {
                    create(p, r, false);
                    break Boot;
                }
                default: {
                    throw new UnexpectedCrash(
                        "Unexpectedly, byte '" + b + "'"
                    );
                }
            }
        }

        Boot:
        // codec
        while (r.also()) {
            if (mutable) Alias:
                while (true) {
                    byte b = r.next();
                    if (b <= 0x20) {
                        switch (b) {
                            case 0x09:
                            case 0x0A:
                            case 0x0D:
                            case 0x20: {
                                continue;
                            }
                        }
                        throw new UnexpectedCrash(
                            "Unexpectedly, byte '" + b + "' <= 32"
                        );
                    }

                    switch (b) {
                        case ':': {
                            break Alias;
                        }
                        case ',': {
                            continue;
                        }
                        case '}': {
                            bundle(p, true);
                            break Alias;
                        }
                        case '"':
                        case '\'': {
                            escape(alias, b, r);
                            continue;
                        }
                        default: {
                            throw new UnexpectedCrash(
                                "Unexpectedly, byte '" + b + "' in alias"
                            );
                        }
                    }
                }

            while (true) {
                byte b = r.next();
                if (b <= 0x20) {
                    switch (b) {
                        case 0x09:
                        case 0x0A:
                        case 0x0D:
                        case 0x20: {
                            continue;
                        }
                    }
                    throw new UnexpectedCrash(
                        "Unexpectedly, byte '" + b + "' <= 32"
                    );
                }

                switch (b) {
                    case '{': {
                        create(p, r, true);
                        continue Boot;
                    }
                    case '[': {
                        create(p, r, false);
                        continue Boot;
                    }
                    case '}': {
                        bundle(p, true);
                        continue Boot;
                    }
                    case ']': {
                        bundle(p, false);
                        continue Boot;
                    }
                    case ',': {
                        continue;
                    }
                    case 'n':
                    case 'N': {
                        escape(r);
                        accept(p, $);
                        continue;
                    }
                    case '"':
                    case '\'': {
                        escape(value, b, r);
                        accept(p, $s);
                        continue Boot;
                    }
                    default: {
                        value.chain(b);
                    }
                }

                while (true) {
                    byte c = r.next();
                    if (c <= 0x20) {
                        switch (c) {
                            case 0x09:
                            case 0x0A:
                            case 0x0D:
                            case 0x20: {
                                continue;
                            }
                        }
                        throw new UnexpectedCrash(
                            "Unexpectedly, byte '" + b + "' <= 32"
                        );
                    }

                    switch (c) {
                        case ',': {
                            accept(p, $);
                            continue Boot;
                        }
                        case '}': {
                            accept(p, $);
                            bundle(p, true);
                            continue Boot;
                        }
                        case ']': {
                            accept(p, $);
                            bundle(p, false);
                            continue Boot;
                        }
                        default: {
                            value.chain(c);
                        }
                    }
                }
            }
        }
    }

    /**
     * Notify to create a receiver
     *
     * @param mark is it a map?
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void create(
        Pipe p,
        Reader r,
        boolean mark
    ) throws IOException {
        if (mask == Long.MIN_VALUE) {
            throw new UnexpectedCrash(
                "Unexpectedly, out of range"
            );
        }

        if (mark) {
            if (p.attach($M, alias)) {
                mask <<= 1;
                data |= mask;
                mutable = true;
            } else {
                dropdown(
                    (byte) '}', r
                );
            }
        } else {
            if (p.attach($L, alias)) {
                mask <<= 1;
                mutable = false;
            } else {
                dropdown(
                    (byte) ']', r
                );
            }
        }
        alias.clean();
    }

    /**
     * Sends data to the current receiver
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void accept(
        Pipe p,
        Space s
    ) throws IOException {
        p.accept(
            s, alias, value
        );
        alias.clean();
        value.clean();
    }

    /**
     * Notify the current receiver to end the transmission
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void bundle(
        Pipe p,
        boolean m
    ) throws IOException {
        if (mutable == m) {
            p.detach();
            mask >>>= 1;
            mutable = (data & mask) != 0L;
        } else {
            throw new UnexpectedCrash(
                "Unexpectedly, mismatched terminator"
            );
        }
    }

    /**
     * Escape special character
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void escape(
        Reader r
    ) throws IOException {
        byte b2 = r.next();
        byte b3 = r.next();
        byte b4 = r.next();

        if ((b2 != 'u' && b2 != 'U') ||
            (b3 != 'l' && b3 != 'L') ||
            (b4 != 'l' && b4 != 'L')) {
            throw new UnexpectedCrash(
                "Unexpectedly, N" +
                    (char) (b2 & 0xFF) +
                    (char) (b3 & 0xFF) +
                    (char) (b3 & 0xFF) + " is not null"
            );
        }
    }

    /**
     * Escape special character
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void escape(
        Chain c,
        byte e,
        Reader r
    ) throws IOException {
        while (true) {
            byte b = r.next();
            if (b == e) {
                break;
            }

            if (b != '\\') {
                c.chain(b);
                continue;
            }

            b = r.next();
            switch (b) {
                case 'r': {
                    b = '\r';
                    break;
                }
                case 'n': {
                    b = '\n';
                    break;
                }
                case 't': {
                    b = '\t';
                    break;
                }
                case 'u': {
                    uncork(c, r);
                    continue;
                }
            }
            c.chain(b);
        }
    }

    /**
     * Filter out the useless
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void dropdown(
        byte a,
        Reader r
    ) throws IOException {
        while (true) {
            byte b = r.next();
            if (a == b) {
                break;
            }
            switch (b) {
                case '{': {
                    dropdown(
                        (byte) '}', r
                    );
                    continue;
                }
                case '[': {
                    dropdown(
                        (byte) ']', r
                    );
                    continue;
                }
                case '"': {
                    Drop:
                    while (true) {
                        switch (r.next()) {
                            case '"': {
                                break Drop;
                            }
                            case '\\': {
                                r.next();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * clear this {@link Mage}
     */
    @Override
    public void clear() {
        data = 0L;
        mask = 1L;
        alias.clean();
        value.clear();
    }

    /**
     * close this {@link Mage}
     */
    @Override
    public void close() {
        data = 0L;
        mask = 1L;
        alias.close();
        value.close();
    }
}
