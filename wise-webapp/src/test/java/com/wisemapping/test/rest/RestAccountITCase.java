/*
*    Copyright [2015] [wisemapping]
*
*   Licensed under WiseMapping Public License, Version 1.0 (the "License").
*   It is basically the Apache License, Version 2.0 (the "License") plus the
*   "powered by wisemapping" text requirement on every single page;
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the license at
*
*       http://www.wisemapping.org/license
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.wisemapping.test.rest;


import com.wisemapping.rest.model.RestUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import java.net.URI;

import static com.wisemapping.test.rest.RestHelper.ADMIN_CREDENTIALS;
import static com.wisemapping.test.rest.RestHelper.BASE_REST_URL;
import static com.wisemapping.test.rest.RestHelper.HOST_PORT;
import static com.wisemapping.test.rest.RestHelper.createHeaders;
import static com.wisemapping.test.rest.RestHelper.createTemplate;
import static org.testng.Assert.assertEquals;


@Test
public class RestAccountITCase {

    @Test(dataProviderClass = RestHelper.class, dataProvider="ContentType-Provider-Function")
    public void deleteUser(final @NotNull MediaType mediaType) {    // Configure media types ...
        final HttpHeaders requestHeaders = createHeaders(mediaType);
        final RestTemplate adminTemplate = createTemplate(ADMIN_CREDENTIALS);

        final RestUser dummyUser = createDummyUser();
        createUser(requestHeaders, adminTemplate, dummyUser);

        // Delete user ...
        final RestTemplate dummyTemplate = createTemplate(dummyUser.getEmail() + ":fooPassword");
        dummyTemplate.delete(BASE_REST_URL + "/account");

        // Is the user there ?
        // Check that the user has been created ...
//        try {
//            findUser(requestHeaders, adminTemplate, location);
//            fail("User could not be deleted !");
//        } catch (Exception e) {
//        }
    }

    public String createNewUser(final @NotNull MediaType mediaType) {

        // Configure media types ...
        final HttpHeaders requestHeaders = createHeaders(mediaType);
        final RestTemplate templateRest = createTemplate(ADMIN_CREDENTIALS);

        // Fill user data ...
        final RestUser restUser = createDummyUser();

        // Create a new user ...
        final URI location = createUser(requestHeaders, templateRest, restUser);

        // Check that the user has been created ...
        ResponseEntity<RestUser> result = findUser(requestHeaders, templateRest, location);
        assertEquals(result.getBody().getEmail(), restUser.getEmail(), "Returned object object seems not be the same.");

        // Find by email and check ...
        result = findUserByEmail(requestHeaders, templateRest, restUser.getEmail());
        assertEquals(result.getBody().getEmail(), restUser.getEmail(), "Returned object object seems not be the same.");

        return restUser.getEmail();
    }


    private ResponseEntity<RestUser> findUser(HttpHeaders requestHeaders, RestTemplate templateRest, URI location) {
        HttpEntity<RestUser> findUserEntity = new HttpEntity<RestUser>(requestHeaders);
        final String url = HOST_PORT + location;
        return templateRest.exchange(url, HttpMethod.GET, findUserEntity, RestUser.class);
    }

    private ResponseEntity<RestUser> findUserByEmail(HttpHeaders requestHeaders, RestTemplate templateRest, final String email) {
        HttpEntity<RestUser> findUserEntity = new HttpEntity<RestUser>(requestHeaders);

        // Add extension only to avoid the fact that the last part is extracted ...
        final String url = BASE_REST_URL + "/admin/users/email/{email}.json";
        return templateRest.exchange(url, HttpMethod.GET, findUserEntity, RestUser.class, email);
    }

    private URI createUser(HttpHeaders requestHeaders, RestTemplate templateRest, RestUser restUser) {
        HttpEntity<RestUser> createUserEntity = new HttpEntity<RestUser>(restUser, requestHeaders);
        return templateRest.postForLocation(BASE_REST_URL + "/admin/users", createUserEntity);
    }

    private RestUser createDummyUser() {
        final RestUser restUser = new RestUser();
        final String username = "foo-to-delete" + System.nanoTime();
        final String email = username + "@example.org";
        restUser.setEmail(email);
        restUser.setFirstname("foo first name");
        restUser.setLastname("foo last name");
        restUser.setPassword("fooPassword");
        return restUser;
    }

}
