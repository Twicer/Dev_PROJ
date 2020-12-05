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

package com.wisemapping.webmvc;


import com.wisemapping.security.Utils;
import com.wisemapping.service.InvalidAuthSchemaException;
import com.wisemapping.service.InvalidUserEmailException;
import com.wisemapping.service.UserService;
import com.wisemapping.view.UserBean;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

@Controller
public class UsersController {

    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Value("${google.recaptcha2.enabled}")
    private boolean captchaEnabled;

    @Value("${google.recaptcha2.siteKey}")
    private String recaptchaSiteKey;

    @RequestMapping(value = "user/resetPassword", method = RequestMethod.GET)
    public ModelAndView showResetPasswordPage() {
        return new ModelAndView("forgotPassword");
    }

    @RequestMapping(value = "user/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@RequestParam(required = true) String email) {

        ModelAndView result;
        try {
            userService.resetPassword(email);
            result = new ModelAndView("forgotPasswordSuccess");

        } catch (InvalidUserEmailException | InvalidAuthSchemaException e) {
            result = new ModelAndView("forgotPasswordError");
        }
        return result;
    }

    @RequestMapping(value = "account/settings", method = RequestMethod.GET)
    public String showUserSettingsPage(@NotNull Model model) {
        model.addAttribute("user", Utils.getUser());
        return "accountSettings";
    }

    @RequestMapping(value = "user/registration", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage() {
        return new ModelAndView("userRegistration");
    }
}
