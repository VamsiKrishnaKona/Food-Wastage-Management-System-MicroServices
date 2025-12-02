package com.helpindia.Admin.controller;

import com.helpindia.Admin.DTOs.AdministratorLoginRequest;
import com.helpindia.Admin.DTOs.AdministratorRegistrationRequest;
import com.helpindia.Admin.model.Administrator;
import com.helpindia.Admin.service.AdministratorService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/admin")
public class AdministratorRestController
{

    private final AdministratorService administratorService;

    @Autowired
    public AdministratorRestController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @GetMapping("/login")
    public ModelAndView loginForm() {
        ModelAndView mv = new ModelAndView("admin/login");
        mv.addObject("loginRequest", new AdministratorLoginRequest());
        return mv;
    }

    // POST /admin/login
    @PostMapping("/login")
    public ModelAndView loginSubmit(
            @Valid @ModelAttribute("loginRequest") AdministratorLoginRequest loginRequest,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes)
    {

        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("admin/login");
            return mv;
        }

        return administratorService.verifyLogin(loginRequest.getEmail(), loginRequest.getPassword())
                .map(admin -> {
                    // safe info in session (no password)
                    session.setAttribute("adminEmail", admin.getEmail());
                    session.setAttribute("adminFullName", admin.getFullName());
                    // redirect to avoid form resubmission
                    return new ModelAndView("redirect:/admin/home");
                })
                .orElseGet(() -> {
                    ModelAndView mv = new ModelAndView("admin/login");
                    mv.addObject("loginError", "Incorrect credentials");
                    return mv;
                });
    }

    // GET /admin/home
    @GetMapping("/home")
    public ModelAndView adminHome(HttpSession session) {
        if (session.getAttribute("adminEmail") == null) {
            return new ModelAndView("redirect:/admin/login");
        }
        ModelAndView mv = new ModelAndView("admin/home");
        mv.addObject("fullName", session.getAttribute("adminFullName"));
        return mv;
    }

    // POST /admin/logout
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("msg", "You have been logged out.");
        return "redirect:/admin/login";
    }

    // Optional: simple registration endpoint (for admin creation)
    @GetMapping("/register")
    public ModelAndView registerForm() {
        ModelAndView mv = new ModelAndView("admin/register");
        mv.addObject("registrationRequest", new AdministratorRegistrationRequest());
        return mv;
    }

    @PostMapping("/register")
    public ModelAndView registerSubmit(
            @Valid @ModelAttribute("registrationRequest") AdministratorRegistrationRequest req,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("admin/register");
        }

        Administrator created = administratorService.createAdministrator(
                req.getEmail(), req.getFirstName(), req.getLastName(), req.getMobileNumber(), req.getPassword());

        redirectAttributes.addFlashAttribute("msg", "Administrator created: " + created.getEmail());
        return new ModelAndView("redirect:/admin/login");
    }
}
