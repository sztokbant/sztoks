package br.net.du.myequity.controller;

import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private UserRepository repository;

    @PostMapping(path = "/new")
    public User createNewUser(@RequestBody final User user) {
        return repository.save(user);
    }

    @GetMapping(path = "/all")
    @ResponseBody
    public List<User> all() {
        return repository.findAll();
    }
}
