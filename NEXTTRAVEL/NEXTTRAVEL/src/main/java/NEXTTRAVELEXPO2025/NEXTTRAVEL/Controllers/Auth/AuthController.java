package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Auth;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Auth.AuthServices;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/Auth")
@RestController
public class AuthController {

    @Autowired
    AuthServices authServices;
    @Autowired
    private JWTUtils jwtUtils;

}
