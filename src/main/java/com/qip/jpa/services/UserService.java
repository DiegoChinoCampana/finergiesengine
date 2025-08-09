package com.qip.jpa.services;


import com.qip.jpa.entities.Cliente;
import com.qip.jpa.entities.User;
import com.qip.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public Cliente getClienteByUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        User user = optionalUser.get();
        Cliente cliente = user.getCliente();

        if (cliente == null) {
            throw new RuntimeException("El usuario no tiene asociado un cliente");
        }

        return cliente;
    }


}
