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

import plus.kat.stream.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static plus.kat.stream.Base64.*;

/**
 * @author kraity
 * @since 0.0.4
 */
public class Dram extends Chain {

    protected Type type;

    /**
     * default
     */
    public Dram() {
        super();
    }

    /**
     * @param size the initial capacity
     */
    public Dram(
        int size
    ) {
        super(size);
    }

    /**
     * @param data the initial byte array
     */
    public Dram(
        @NotNull byte[] data
    ) {
        super(data);
        count = data.length;
    }

    /**
     * @param chain specify the {@link Chain} to be mirrored
     */
    public Dram(
        @NotNull Chain chain
    ) {
        super(chain);
    }

    /**
     * @param bucket the specified {@link Bucket} to be used
     */
    public Dram(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * @param sequence specify the {@link CharSequence} to be mirrored
     */
    public Dram(
        @Nullable CharSequence sequence
    ) {
        super();
        if (sequence != null) {
            chain(
                sequence, 0, sequence.length()
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
     */
    public void setType(
        @Nullable Type type
    ) {
        this.type = type;
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
     * Returns a {@code REC4648|Basic} encoded String of {@link Dram}
     */
    @NotNull
    public String asBase64() {
        return Binary.latin(
            toBase64()
        );
    }

    /**
     * Returns a {@code REC4648|Basic} encoded byte array of {@link Dram}
     */
    @NotNull
    public byte[] toBase64() {
        return REC4648.INS.encode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code REC4648|Basic} decoded byte array of {@link Dram}
     */
    @NotNull
    public byte[] byBase64() {
        return REC4648.INS.decode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC4648_SAFE|URL/Filename Safe} encoded String of {@link Dram}
     */
    @NotNull
    public String asBaseSafe() {
        return Binary.latin(
            toBaseSafe()
        );
    }

    /**
     * Returns a {@code RFC4648_SAFE|URL/Filename Safe} encoded byte array of {@link Dram}
     */
    @NotNull
    public byte[] toBaseSafe() {
        return RFC4648_SAFE.INS.encode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC4648_SAFE|URL/Filename Safe} decoded byte array of {@link Dram}
     */
    @NotNull
    public byte[] byBaseSafe() {
        return RFC4648_SAFE.INS.decode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC2045|Mime} encoded String of {@link Dram}
     */
    @NotNull
    public String asBaseMime() {
        return Binary.latin(
            toBaseMime()
        );
    }

    /**
     * Returns a {@code RFC2045|Mime} encoded byte array of {@link Dram}
     */
    @NotNull
    public byte[] toBaseMime() {
        return RFC2045.INS.encode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC2045|Mime} decoded byte array of {@link Dram}
     */
    @NotNull
    public byte[] byBaseMime() {
        return RFC2045.INS.decode(
            value, 0, count
        );
    }

    /**
     * Parses this {@link Dram} as a {@link BigDecimal}
     *
     * @return the specified {@link BigDecimal}, {@code 'ZERO'} on error
     */
    @NotNull
    public BigDecimal toBigDecimal() {
        int size = count;
        if (size != 0) {
            byte[] it = value;
            char[] ch = new char[size];
            while (--size != -1) {
                ch[size] = (char) (
                    it[size] & 0xFF
                );
            }
            try {
                return new BigDecimal(ch);
            } catch (Exception e) {
                // Nothing
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Parses this {@link Dram} as a {@link BigInteger}
     *
     * @return the specified {@link BigInteger}, {@code 'ZERO'} on error
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public BigInteger toBigInteger() {
        int size = count;
        if (size != 0) {
            long num = toLong();
            if (num != 0) {
                return BigInteger.valueOf(num);
            }
            try {
                return new BigInteger(
                    new String(
                        value, 0, 0, size
                    )
                );
            } catch (Exception e) {
                // Nothing
            }
        }
        return BigInteger.ZERO;
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
     */
    protected void clean() {
        hash = 0;
        star = 0;
        count = 0;
        type = null;
        backup = null;
    }
}
