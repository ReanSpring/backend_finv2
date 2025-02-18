package com.example.finv2.controller;

import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.model.Newthing;
import com.example.finv2.service.NewthingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newthing")
public class NewthingController {
    private final NewthingService newthingService;

    public NewthingController(NewthingService newthingService) {
        this.newthingService = newthingService;
    }

    @GetMapping
    public ResponseDTO<List<Newthing>> getAllNewthings(@RequestHeader("Authorization") String token){
        return new ResponseDTO<>("Get all newthing success", newthingService.findAllNew(token), "200");
    }

    @PostMapping
    public ResponseDTO<Newthing> addNewthing(@RequestBody Newthing newthing, @RequestHeader("Authorization") String token){
        return new ResponseDTO<>("Add newthing success", newthingService.createNewthing(newthing, token), "200");
    }

   @PutMapping("/{id}")
public ResponseDTO<Newthing> updateNewthing(@PathVariable Long id, @RequestBody Newthing newthing, @RequestHeader("Authorization") String token){
    return new ResponseDTO<>("Update newthing success", newthingService.updateNewthing(id, newthing, token), "200");
}

    @DeleteMapping("/{id}")
    public ResponseDTO<String> deleteNewthing(@PathVariable Long id, @RequestHeader("Authorization") String token){
        newthingService.deleteNewthing(id, token);
        return new ResponseDTO<>("Delete newthing success", null, "200");
    }

}
