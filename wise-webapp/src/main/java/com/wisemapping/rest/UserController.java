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

package com.wisemapping.rest;

import com.wisemapping.exceptions.EmailNotExistsException;
import com.wisemapping.exceptions.WiseMappingException;
import com.wisemapping.model.AuthenticationType;
import com.wisemapping.model.User;
import com.wisemapping.rest.model.RestUserRegistration;
import com.wisemapping.service.*;
import com.wisemapping.validator.Messages;
import com.wisemapping.validator.UserValidator;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin
public class UserController extends BaseController {
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Autowired
    private RecaptchaService captchaService;

    @Value("${google.recaptcha2.enabled}")
    private boolean captchaEnabled;

    @Value("${google.recaptcha2.siteKey}")
    private String recaptchaSiteKey;

    final Logger logger = Logger.getLogger(UserController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/users", produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void registerUser(@RequestBody RestUserRegistration registration, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws WiseMappingException, BindException {
        logger.info("Register new user:" + registration.getEmail());

        verify(registration, request.getRemoteAddr());

        final User user = new User();
        user.setEmail(registration.getEmail().trim());
        user.setFirstname(registration.getFirstname());
        user.setLastname(registration.getLastname());
        user.setPassword(registration.getPassword());

        user.setAuthenticationType(AuthenticationType.DATABASE);
        userService.createUser(user, false, true);
        response.setHeader("Location", "/service/users/" + user.getId());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/users/resetPassword", produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.OK)
    public void resetPassword(@RequestParam String email) throws InvalidAuthSchemaException, EmailNotExistsException {
        try {
            userService.resetPassword(email);
        }catch (InvalidUserEmailException e){
            throw new EmailNotExistsException(e);
        }
    }

    private void verify(@NotNull final RestUserRegistration registration, @NotNull String remoteAddress) throws BindException {

        final BindException errors = new RegistrationException(registration, "registration");
        final UserValidator validator = new UserValidator();
        validator.setUserService(userService);
        validator.validate(registration, errors);

        // If captcha is enabled, generate it ...
        if (captchaEnabled) {
            final String recaptcha = registration.getRecaptcha();
            if (recaptcha != null) {
                final String reCaptchaResponse = captchaService.verifyRecaptcha(recaptcha, remoteAddress);
                if (!reCaptchaResponse.isEmpty()) {
                    errors.rejectValue("recaptcha", reCaptchaResponse);
                }
            } else {
                errors.rejectValue("recaptcha", Messages.CAPTCHA_LOADING_ERROR);
            }
        }else {
            logger.warn("captchaEnabled is enabled.Recommend to enable it for production environments.");
        }

        if (errors.hasErrors()) {
            throw errors;
        }
    }
}
