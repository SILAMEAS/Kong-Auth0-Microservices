package com.silacode.dto.res;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRes {
    String name;
    int age;
    String address;
    String phone;
    String email;
}
