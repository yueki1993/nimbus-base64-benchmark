/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

// same package as nimbus' Base64Codec
package com.nimbusds.jose.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
@Fork(1)
public class MyBenchmark {

  private static final int SMALL_LENGTH = 100;

  private static final int MEDIUM_LENGTH = 1000;

  private static final int LARGE_LENGTH = 10000;

  private static final int N = 10000; // the number of test strings for each length

  private List<String> smallB64Strings = new ArrayList<>();

  private List<String> mediumB64Strings = new ArrayList<>();

  private List<String> largeB64Strings = new ArrayList<>();

  private int pos = 0;

  private static final boolean NEED_ASSERTION = false;

  @Setup
  public void setup() {
    System.out.println("setup");
    for (int i = 0; i < N; i++) {
      smallB64Strings.add(getRandomB64UrlString(SMALL_LENGTH));
      mediumB64Strings.add(getRandomB64UrlString(MEDIUM_LENGTH));
      largeB64Strings.add(getRandomB64UrlString(LARGE_LENGTH));
    }

    if (NEED_ASSERTION) {
      System.out.println("assert");
      for (int i = 0; i < N; i++) {
        assertMatch(smallB64Strings.get(i));
        assertMatch(mediumB64Strings.get(i));
        assertMatch(largeB64Strings.get(i));
      }
    }
  }

  private static String getRandomB64UrlString(int len) {
    return Base64.getUrlEncoder().encodeToString(RandomStringUtils.random(len).getBytes());
  }

  private static void assertMatch(String s) {
    if (!Arrays.equals(java8B64UrlDecode(s), nimbusB64UrlDecode(s)) ||
        !Arrays.equals(nimbusB64UrlDecode(s), nimbusB64UrlDecodeCodec(s))) {
      throw new RuntimeException("unmatch!");
    }
  }
  // =================== benchmark Java8 java.util.Base64 ====================================

  @Benchmark
  public void java8B64Decoder_small() {
    String b64 = getStringFromList(smallB64Strings);
    java8B64UrlDecode(b64);
  }

  @Benchmark
  public void java8B64Decoder_medium() {
    String b64 = getStringFromList(mediumB64Strings);
    java8B64UrlDecode(b64);
  }

  @Benchmark
  public void java8B64Decoder_large() {
    String b64 = getStringFromList(largeB64Strings);
    java8B64UrlDecode(b64);
  }

  private static byte[] java8B64UrlDecode(String s) {
    return Base64.getUrlDecoder().decode(s);
  }

  // =================== benchmark nimbus base64 ====================================

  @Benchmark
  public void nimbusB64Decoder_small() {
    String b64 = getStringFromList(smallB64Strings);
    nimbusB64UrlDecode(b64);
  }

  @Benchmark
  public void nimbusB64Decoder_medium() {
    String b64 = getStringFromList(mediumB64Strings);
    nimbusB64UrlDecode(b64);
  }

  @Benchmark
  public void nimbusB64Decoder_large() {
    String b64 = getStringFromList(largeB64Strings);
    nimbusB64UrlDecode(b64);
  }

  private static byte[] nimbusB64UrlDecode(String s) {
    return new com.nimbusds.jose.util.Base64(s).decode();
  }

  // =================== benchmark nimbus Base64Codec ====================================

  @Benchmark
  public void nimbusB64DecoderCodec_small() {
    String b64 = getStringFromList(smallB64Strings);
    nimbusB64UrlDecodeCodec(b64);
  }

  @Benchmark
  public void nimbusB64DecoderCodec_medium() {
    String b64 = getStringFromList(mediumB64Strings);
    nimbusB64UrlDecodeCodec(b64);
  }

  @Benchmark
  public void nimbusB64DecoderCodec_large() {
    String b64 = getStringFromList(largeB64Strings);
    nimbusB64UrlDecodeCodec(b64);
  }

  private static byte[] nimbusB64UrlDecodeCodec(String s) {
    return Base64Codec.decode(s);
  }


  private String getStringFromList(List<String> list) {
    String r = list.get(pos++);
    if (pos == N) {
      pos = 0;
    }
    return r;
  }
}
