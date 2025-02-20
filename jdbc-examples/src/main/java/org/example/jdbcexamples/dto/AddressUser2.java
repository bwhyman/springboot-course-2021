package org.example.jdbcexamples.dto;


import org.example.jdbcexamples.dox.Address;
import org.example.jdbcexamples.dox.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressUser2 {
    private User user;
    private Address address;
}
