package com.example.splitExpenses.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int friendId;
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "friend")
    private List<Payment> payments;
}
