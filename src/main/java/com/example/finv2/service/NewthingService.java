package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Daily;
import com.example.finv2.model.Newthing;
import com.example.finv2.model.User;
import com.example.finv2.repo.NewthingRepo;
import com.example.finv2.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class NewthingService {
    private final NewthingRepo newthingRepo;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    public NewthingService(NewthingRepo newthingRepo, JwtUtil jwtUtil, UserRepo userRepo) {
        this.newthingRepo = newthingRepo;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    public List<Newthing> findAllNew(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            List<Newthing> newthings = newthingRepo.findAllByUser(currentUser);
            newthings.sort((d1, d2) -> d2.getDate().compareTo(d1.getDate()));
            return newthings;
        }
        return null;
    }

//    public List<Balance> findAllBalance(String token) {
//    String username = jwtUtil.extractUsername(token.substring(7));
//    User currentUser = userRepo.findUserByEmail(username).orElse(null);
//    if (currentUser != null) {
//        List<Balance> balances = balanceRepo.findAllByUser(currentUser);
//        Collections.reverse(balances);
//        return balances;
//    }
//    return null;

    public Newthing createNewthing(Newthing newthing, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (newthing.getDate() == null) {
            newthing.setDate(LocalDate.now());
        }
        newthing.setUser(currentUser);
        return newthingRepo.save(newthing);
    }

  public Newthing updateNewthing(Long id, Newthing newthing, String token) {
    String username = jwtUtil.extractUsername(token.substring(7));
    User currentUser = userRepo.findUserByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Newthing existingNewthing = newthingRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Newthing not found"));

    if (newthing.getDate() == null) {
        newthing.setDate(LocalDate.now());
    }

    existingNewthing.setDate(newthing.getDate());
    existingNewthing.setUser(currentUser);
    existingNewthing.setName(newthing.getName()); // Update other fields as needed

    return newthingRepo.save(existingNewthing);
}

    public void deleteNewthing(Long id, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Newthing newthing = newthingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Newthing not found"));

        if (!newthing.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Unauthorized");
        }

        newthingRepo.delete(newthing);
    }
}
