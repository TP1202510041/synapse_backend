package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Mostrar formulario de login
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Procesar login
    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        if (authService.loginUser(email, password)) {
            return "redirect:/dashboard"; // Login exitoso
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login"; // Volver al login
        }
    }

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // necesitarías crear dashboard.html
    }

    // Procesar registro
    @PostMapping("/register")
    public String processRegister(@RequestParam String email,
                                  @RequestParam String userName,
                                  @RequestParam String password,
                                  Model model) {
        if (authService.registerUser(email, userName, password)) {
            model.addAttribute("success", "Usuario registrado exitosamente");
            return "login"; // Redirigir al login después del registro
        } else {
            model.addAttribute("error", "El email ya está registrado");
            return "register"; // Volver al registro con error
        }
    }
}