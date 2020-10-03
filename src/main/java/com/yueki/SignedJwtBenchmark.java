package com.yueki;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;


/**
 * This benchmark is based on com.nimbusds.jw.SignedJWTTest#testSignAndVerify.
 *
 * The original code license statement:
 *
 * Nimbus JOSE + JWT
 *
 * Copyright 2012 - 2018, Connect2id Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
@State(Scope.Thread)
@Fork(1)
public class SignedJwtBenchmark {

  private static final int SMALL_LENGTH = 100;

  private static final int MEDIUM_LENGTH = 1000;

  private static final int LARGE_LENGTH = 10000;

  private static final int N = 100; // the number of test strings for each length

  private List<String> smallJwtStrings = new ArrayList<>();

  private List<String> mediumJwtStrings = new ArrayList<>();

  private List<String> largeJwtStrings = new ArrayList<>();


  private RSAPublicKey publicKey;
  private RSAPrivateKey privateKey;

  @Setup
  public void setup() throws Exception {
    System.out.println("setup");

    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);

    KeyPair kp = kpg.genKeyPair();
    publicKey = (RSAPublicKey) kp.getPublic();
    privateKey = (RSAPrivateKey) kp.getPrivate();

    for (int i = 0; i < N; i++) {
      smallJwtStrings.add(getSignedJwt(SMALL_LENGTH));
      mediumJwtStrings.add(getSignedJwt(MEDIUM_LENGTH));
      largeJwtStrings.add(getSignedJwt(LARGE_LENGTH));
    }
  }


  private String getSignedJwt(int len) throws Exception {
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(RandomStringUtils.randomAscii(len))
        .issueTime(new Date(123000L))
        .issuer("https://c2id.com")
        .claim("scope", "openid")
        .build();

    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).
        keyID("1").
        jwkURL(new URI("https://c2id.com/jwks.json")).
        build();

    SignedJWT signedJWT = new SignedJWT(header, claimsSet);
    JWSSigner signer = new RSASSASigner(privateKey);
    signedJWT.sign(signer);

    return signedJWT.serialize();
  }

  private JWTClaimsSet parseSignedJwt(String serializedJWT) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(serializedJWT);

      JWSVerifier verifier = new RSASSAVerifier(publicKey);
      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("invalid token!");
      }
      return signedJWT.getJWTClaimsSet();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  // =================== benchmark ====================================

  @Benchmark
  public void JwtParse_small() {
    String s = getStringFromList(smallJwtStrings);
    parseSignedJwt(s);
  }

  @Benchmark
  public void JwtParse_medium() {
    String s = getStringFromList(mediumJwtStrings);
    parseSignedJwt(s);
  }

  @Benchmark
  public void JwtParse_large() {
    String s = getStringFromList(largeJwtStrings);
    parseSignedJwt(s);
  }

  private int pos = 0;

  private String getStringFromList(List<String> list) {
    String r = list.get(pos++);
    if (pos == N) {
      pos = 0;
    }
    return r;
  }
}
