package com.safereach.inventory_system;

import org.springframework.boot.SpringApplication;

public class TestInventorySystemApplication {

	public static void main(String[] args) {
		SpringApplication.from(InventorySystemApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
