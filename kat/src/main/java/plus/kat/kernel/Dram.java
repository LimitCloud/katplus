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
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.stream.*;
import plus.kat.utils.Config;

import java.lang.reflect.Type;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author kraity
 * @since 0.0.4
 */
public class Dram extends Chain {

    protected Type type;

    /**
     * Constructs an empty dram
     */
    public Dram() {
        super();
    }

    /**
     * Constructs a dram with the specified size
     *
     * @param size the initial capacity
     */
    public Dram(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs an empty dram
     *
     * @param fixed the specified state
     */
    public Dram(
        boolean fixed
    ) {
        super(fixed);
    }

    /**
     * Constructs a dram with the specified data
     *
     * @param data the initial byte array
     */
    public Dram(
        @NotNull byte[] data
    ) {
        super(data);
        count = data.length;
    }

    /**
     * Constructs a dram with the specified chain
     *
     * @param chain the specified chain to be used
     */
    public Dram(
        @NotNull Chain chain
    ) {
        super(chain);
    }

    /**
     * Constructs an empty dram with the specified chain
     *
     * @param bucket the specified bucket to be used
     */
    public Dram(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Constructs a dram with the specified chain and bucket
     *
     * @param chain  the specified chain to be used
     * @param bucket the specified bucket to be used
     */
    public Dram(
        @NotNull Chain chain,
        @Nullable Bucket bucket
    ) {
        super(chain);
        this.bucket = bucket;
    }

    /**
     * Constructs a chain with the specified sequence
     *
     * @param sequence the specified sequence to be used
     */
    public Dram(
        @Nullable CharSequence sequence
    ) {
        super();
        if (sequence != null) {
            int len = sequence.length();
            if (len != 0) {
                chain(
                    sequence, 0, len
                );
                star |= 2;
                backup = sequence.toString();
            }
        }
    }

    /**
     * Appends the byte value
     *
     * @param b the specified byte value
     * @throws Collapse If the dram is finally fixed
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void add(
        byte b
    ) {
        if (0 <= star) {
            byte[] it = value;
            if (count != it.length) {
                star = 0;
                it[count++] = b;
            } else {
                grow(count + 1);
                star = 0;
                value[count++] = b;
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the dram is finally fixed"
            );
        }
    }

    /**
     * Sets the value of the specified location
     *
     * @param i the specified index
     * @param b the specified value
     * @throws Collapse                       If the dram is finally fixed
     * @throws ArrayIndexOutOfBoundsException if the index argument is negative
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void set(
        int i, byte b
    ) {
        if (0 <= star) {
            byte[] it = value;
            if (i < it.length) {
                star = 0;
                it[i] = b;
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the dram is finally fixed"
            );
        }
    }

    /**
     * Returns the modifier type
     */
    @Nullable
    public Type getType() {
        return type;
    }

    /**
     * Sets the modifier type of {@link Dram}
     *
     * @param type the specified type
     * @throws Collapse If the dram is read-only
     * @see Chain#isFixed()
     */
    public void setType(
        @Nullable Type type
    ) {
        if (0 <= star) {
            this.type = type;
        } else {
            throw new Collapse(
                "Unexpectedly, the dram is read-only"
            );
        }
    }

    /**
     * Returns a {@link Dram} of this {@link Dram}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Dram subSequence(
        int start, int end
    ) {
        return new Dram(
            toBytes(start, end)
        );
    }

    /**
     * Returns a SecretKeySpec, please check {@link #length()}
     *
     * @throws IllegalArgumentException If the algo is null
     */
    @NotNull
    public SecretKeySpec asSecretKeySpec(
        @NotNull String algo
    ) {
        return new SecretKeySpec(
            value, 0, count, algo
        );
    }

    /**
     * Returns a SecretKeySpec, please check {@code offset}, {@code algo} and {@code length}
     *
     * @throws IllegalArgumentException       If the algo is null or the offset out of range
     * @throws ArrayIndexOutOfBoundsException If the length is negative
     */
    @NotNull
    public SecretKeySpec asSecretKeySpec(
        @NotNull String algo, int offset, int length
    ) {
        return new SecretKeySpec(
            value, offset, length, algo
        );
    }

    /**
     * Returns a IvParameterSpec, please check {@link #length()}
     */
    @NotNull
    public IvParameterSpec asIvParameterSpec() {
        return new IvParameterSpec(
            value, 0, count
        );
    }

    /**
     * Returns a IvParameterSpec, please check {@code offset} and {@code length}
     *
     * @throws IllegalArgumentException       If the offset out of range
     * @throws ArrayIndexOutOfBoundsException If the length is negative
     */
    @NotNull
    public IvParameterSpec asIvParameterSpec(
        int offset, int length
    ) {
        return new IvParameterSpec(
            value, offset, length
        );
    }

    /**
     * Clean this {@link Dram}
     *
     * @throws Collapse If the dram doesn't own bucket
     * @since 0.0.5
     */
    public void clean() {
        if (bucket != null) {
            hash = 0;
            star = 0;
            count = 0;
            type = null;
            backup = null;
        } else {
            throw new Collapse(
                "Unexpectedly, the dram doesn't own bucket"
            );
        }
    }

    /**
     * Clear this {@link Dram}
     *
     * @throws Collapse If the dram doesn't own bucket
     * @since 0.0.5
     */
    public void clear() {
        Bucket bt = bucket;
        if (bt != null) {
            hash = 0;
            star = 0;
            count = 0;
            type = null;
            backup = null;
            byte[] it = value;
            if (it.length != 0) {
                value = bt.swop(it);
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the dram doesn't own bucket"
            );
        }
    }

    /**
     * Close this {@link Dram}
     *
     * @throws Collapse If the dram doesn't own bucket
     * @since 0.0.5
     */
    public void close() {
        Bucket bt = bucket;
        if (bt != null) {
            hash = 0;
            star = 0;
            count = 0;
            type = null;
            backup = null;
            byte[] it = value;
            if (it.length != 0) {
                bt.share(it);
                value = EMPTY_BYTES;
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the dram doesn't own bucket"
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static class Memory implements Bucket {

        public static final int SIZE, SCALE;

        static {
            SIZE = Config.get(
                "kat.memory.size", 4
            );
            SCALE = Config.get(
                "kat.memory.scale", 1024 * 4
            );
        }

        public static final Memory
            INS = new Memory();

        private final byte[][]
            bucket = new byte[SIZE][];

        @NotNull
        public byte[] alloc() {
            Thread th = Thread.currentThread();
            int tr = th.hashCode() & 0xFFFFFF;

            byte[] it;
            int ix = tr % SIZE;

            synchronized (this) {
                it = bucket[ix];
                bucket[ix] = null;
            }

            if (it != null &&
                SCALE <= it.length) {
                return it;
            }

            return new byte[SCALE];
        }

        @Override
        public boolean share(
            @Nullable byte[] it
        ) {
            if (it != null && SCALE == it.length) {
                Thread th = Thread.currentThread();
                int ix = (th.hashCode() & 0xFFFFFF) % SIZE;
                synchronized (this) {
                    bucket[ix] = it;
                }
                return true;
            }
            return false;
        }

        @Override
        public byte[] swop(
            @Nullable byte[] it
        ) {
            this.share(it);
            return EMPTY_BYTES;
        }

        @Override
        public byte[] apply(
            @NotNull byte[] it, int len, int size
        ) {
            Thread th = Thread.currentThread();
            int ix = (th.hashCode() & 0xFFFFFF) % SIZE;

            byte[] data;
            if (size <= SCALE) {
                synchronized (this) {
                    data = bucket[ix];
                    bucket[ix] = null;
                }
                if (data == null ||
                    SCALE > it.length) {
                    data = new byte[SCALE];
                }
                if (it.length != 0) {
                    System.arraycopy(
                        it, 0, data, 0, len
                    );
                }
            } else {
                int cap = it.length +
                    (it.length >> 1);
                if (cap < size) {
                    cap = size;
                }
                data = new byte[cap];
                if (it.length != 0) {
                    System.arraycopy(
                        it, 0, data, 0, len
                    );

                    if (SCALE == it.length) {
                        synchronized (this) {
                            bucket[ix] = it;
                        }
                    }
                }
            }

            return data;
        }
    }
}
