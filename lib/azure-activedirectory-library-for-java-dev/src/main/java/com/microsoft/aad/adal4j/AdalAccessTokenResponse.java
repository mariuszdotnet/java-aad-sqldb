// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.microsoft.aad.adal4j;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import net.minidev.json.JSONObject;


/**
 * 
 */
class AdalAccessTokenResponse extends OIDCTokenResponse {

    private String resource;

    AdalAccessTokenResponse(final AccessToken accessToken,
                            final RefreshToken refreshToken, final String idToken) {
        super(new OIDCTokens(idToken, accessToken, refreshToken));
    }

    AdalAccessTokenResponse(final AccessToken accessToken,
            final RefreshToken refreshToken, final String idToken,
            final String resource) {
        this(accessToken, refreshToken, idToken);
        this.resource = resource;
    }

    String getResource() {
        return resource;
    }

    /**
     * 
     * @param httpResponse
     * @return
     * @throws ParseException
     */
    static AdalAccessTokenResponse parseHttpResponse(
            final HTTPResponse httpResponse) throws ParseException {

        httpResponse.ensureStatusCode(HTTPResponse.SC_OK);

        final JSONObject jsonObject = httpResponse.getContentAsJSONObject();

        return parseJsonObject(jsonObject);
    }

    /**
     * 
     * @param jsonObject
     * @return
     * @throws ParseException
     */
    static AdalAccessTokenResponse parseJsonObject(final JSONObject jsonObject)
            throws ParseException {

        final AccessToken accessToken = AccessToken.parse(jsonObject);
        final RefreshToken refreshToken = RefreshToken.parse(jsonObject);

        // In same cases such as client credentials there isn't an id token. Instead of a null value
        // use an empty string in order to avoid an IllegalArgumentException from OIDCTokens.
        String idTokenValue = "";
        if (jsonObject.containsKey("id_token")) {
            idTokenValue = JSONObjectUtils.getString(jsonObject, "id_token");
        }

        // Parse value
        String resourceValue = null;
        if (jsonObject.containsKey("resource")) {
            resourceValue = JSONObjectUtils.getString(jsonObject, "resource");
        }

        return new AdalAccessTokenResponse(accessToken, refreshToken,
                idTokenValue, resourceValue);
    }
}
