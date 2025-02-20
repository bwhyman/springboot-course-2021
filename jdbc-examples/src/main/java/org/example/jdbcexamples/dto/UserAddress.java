package org.example.jdbcexamples.dto;

import org.example.jdbcexamples.dox.Address;
import org.example.jdbcexamples.dox.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddress {
    private User user;
    private List<Address> addresses;
}
