package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, RedirectAttributes ra) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        ra.addFlashAttribute("message", "Registration successful! Please login.");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(String email, RedirectAttributes ra) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            // In a real app, you'd send an email here. 
            // For now, we redirect to a reset page for that specific email.
            ra.addAttribute("email", email);
            return "redirect:/reset-password";
        }
        ra.addFlashAttribute("error", "Email not found.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(String email, Model model) {
        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String finishReset(String email, String newPassword, RedirectAttributes ra) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            ra.addFlashAttribute("message", "Password reset successful! Please login.");
            return "redirect:/login";
        }
        return "redirect:/login";
    }
}